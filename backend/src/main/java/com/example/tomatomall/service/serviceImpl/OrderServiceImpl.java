package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.po.*;
import com.example.tomatomall.repository.*;
import com.example.tomatomall.service.OrderService;
import com.example.tomatomall.util.DateUtil;
import com.example.tomatomall.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.tomatomall.util.TokenUtil;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.example.tomatomall.po.Account;
import com.example.tomatomall.po.Product;

import java.util.ArrayList;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    TokenUtil tokenUtil;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockpileRepository stockpileRepository;

    @Autowired
    private CartOrderRelationRepository cartOrderRelationRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private static final int LOCK_TIME_MINUTES = 30; // 库存锁定时间，单位：分钟

    // 内存中维护的订单商品锁定信息 <orderId, Map<productId, quantity>>
    private final Map<Integer, Map<Integer, Integer>> orderProductLocks = new HashMap<>();

    @Override
    @Transactional
    public Order createOrder(String username, List<Integer> cartItemIds, ShippingAddress shippingAddress, String payment_method, Integer couponId) {
        // 1. 验证cartItemIds是否为空
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            throw new IllegalArgumentException("cartItemIds不能为空");
        }
        // 2. 获取用户购物车商品，并验证商品是否存在
        List<Cart> carts = cartRepository.findAllById(cartItemIds);
        if (carts.size() != cartItemIds.size()) {
            throw new IllegalArgumentException("购物车商品不存在");
        }
        // 3. 验证用户是否一致, 并获取用户ID
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        Integer userId = account.getId();

        // 4. 验证库存，计算订单原始总金额，并尝试锁定库存
        BigDecimal originalTotalAmount = BigDecimal.ZERO;// 原始总金额
        Map<Integer, Integer> currentOrderLocks = new HashMap<>();
        for (Cart cart : carts) {
            Product product = cart.getProduct();
            Integer quantity = cart.getQuantity();
            Stockpile stockpile = stockpileRepository.findByProductId(product.getId());
            if (stockpile == null || stockpile.getAmount() - stockpile.getFrozen() < quantity) {
                throw new IllegalArgumentException("商品" + product.getTitle() + "库存不足");
            }
            stockpile.setFrozen(stockpile.getFrozen() + quantity);
            stockpileRepository.save(stockpile); // 更新冻结库存
            currentOrderLocks.put(product.getId(), quantity);
            originalTotalAmount = originalTotalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        }

        BigDecimal discountedTotalAmount = originalTotalAmount;
        Coupon usedCoupon = null;

        // 5. 处理优惠券
        if (couponId != null) {
            Optional<Coupon> couponOptional = couponRepository.findById(couponId);
            if (couponOptional.isPresent()) {
                Coupon coupon = couponOptional.get();
                LocalDateTime now = LocalDateTime.now();

                // 5.1. 验证优惠券状态、有效期和最低消费金额
                if (!coupon.getStatus().equals("ACTIVE") || coupon.getValidFrom().isAfter(now) || coupon.getValidUntil().isBefore(now)) {
                    throw new IllegalArgumentException("优惠券不可用");
                }
                if (coupon.getMinPurchaseAmount() != null && originalTotalAmount.compareTo(coupon.getMinPurchaseAmount()) < 0) {
                    throw new IllegalArgumentException("订单金额未达到优惠券最低消费金额");
                }

                // 5.2. 验证用户是否已领取且未使用该优惠券
                Optional<UserCoupon> userCouponOptional = userCouponRepository.findByUserIdAndCouponIdAndStatus(userId, couponId, "UNUSED");
                if (!userCouponOptional.isPresent()) {
                    throw new IllegalArgumentException("该优惠券您尚未领取或已被使用");
                }
                UserCoupon userCoupon = userCouponOptional.get();

                // 5.3. 计算折扣后的金额
                switch (coupon.getCouponType()) {
                    case "FIXED":
                        discountedTotalAmount = discountedTotalAmount.subtract(coupon.getDiscountAmount());
                        if (discountedTotalAmount.compareTo(BigDecimal.ZERO) < 0) {
                            discountedTotalAmount = BigDecimal.ZERO; // 保证金额不为负数
                        }
                        break;
                    case "PERCENTAGE":
                        discountedTotalAmount = discountedTotalAmount.multiply(BigDecimal.ONE.subtract(coupon.getDiscountPercentage()));
                        break;
                    case "THRESHOLD": // 满减类型已经在最低消费金额处验证
                        discountedTotalAmount = discountedTotalAmount.subtract(coupon.getDiscountAmount());
                        if (discountedTotalAmount.compareTo(BigDecimal.ZERO) < 0) {
                            discountedTotalAmount = BigDecimal.ZERO; // 保证金额不为负数
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("不支持的优惠券类型");
                }
                usedCoupon = coupon;

                // 5.4. 更新 UserCoupon 状态为已使用
                userCoupon.setStatus("USED");
                userCoupon.setUsedTime(LocalDateTime.now());
                userCouponRepository.save(userCoupon);
            }
        }

        // 6. 创建订单
        Order order = new Order();
        order.setTotalAmount(discountedTotalAmount); // 使用折扣后的金额
        order.setPayment_method(payment_method);
        order.setStatus("PENDING"); // 初始状态为待支付
        order.setCreateTime(new Date());
        order.setAccount(accountRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在")));
        if (usedCoupon != null) {
            order.setCouponId(usedCoupon.getCouponId());
            order.setCoupon(usedCoupon);
        }
        orderRepository.save(order);

        // 7. 记录订单的商品锁定信息
        orderProductLocks.put(order.getOrderId(), currentOrderLocks);

        // 8. 创建订单和购物车项的关联关系, 并保存
        for (Cart cart : carts) {
            CartOrderRelation cartOrderRelation = new CartOrderRelation();
            cartOrderRelation.setCart(cart);
            cartOrderRelation.setOrder(order);
            cartOrderRelationRepository.save(cartOrderRelation); // 保存关联关系
        }

        // 9. 启动定时任务，如果订单在指定时间内未支付，则释放锁定的库存
        scheduleReleaseStock(order.getOrderId(), currentOrderLocks); // 传递 currentOrderLocks

        return order;
    }

    private void scheduleReleaseStock(Integer orderId, Map<Integer, Integer> lockedProducts) {
        Date releaseTime = new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(LOCK_TIME_MINUTES));
        scheduler.schedule(() -> {
            Order order = orderRepository.findByOrderId(orderId);
            if (order != null && !"SUCCESS".equals(order.getStatus()) && !"CLOSED".equals(order.getStatus())) {
                // 订单未支付且未关闭，释放库存
                if (lockedProducts != null) {
                    for (Map.Entry<Integer, Integer> entry : lockedProducts.entrySet()) {
                        Integer productId = entry.getKey();
                        Integer quantity = entry.getValue();
                        Stockpile stockpile = stockpileRepository.findByProductId(productId);
                        if (stockpile != null) {
                            stockpile.setFrozen(Math.max(0, stockpile.getFrozen() - quantity)); // 释放冻结库存
                            stockpileRepository.save(stockpile);
                            System.out.println("订单 " + orderId + " 超时未支付，释放商品 " + productId + " 的锁定库存 " + quantity);
                        }
                    }
                    orderProductLocks.remove(orderId);
                    order.setStatus("TIMEOUT"); // 更新订单状态为超时
                    orderRepository.save(order);
                    System.out.println("订单 " + orderId + " 状态更新为 TIMEOUT");
                }
            }
        }, LOCK_TIME_MINUTES, TimeUnit.MINUTES);
        System.out.println("订单 " + orderId + " 的库存锁定将在 " + DateUtil.formatDate(releaseTime) + " 后自动释放");
    }

    @Override
    @Transactional
    public void handlePaymentSuccess(Integer orderId, String alipayTradeNo, String totalAmount) {
        // 1. 验证订单是否存在
        Order order = orderRepository.findByOrderId(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在: " + orderId);
        }

        // 2. 验证订单状态
        if (!"PENDING".equals(order.getStatus())) {
            System.out.println("订单 " + orderId + " 已经处理过，当前状态: " + order.getStatus());
            return; // 幂等性处理：避免重复处理
        }

        // 3. 验证支付金额
        BigDecimal paidAmount = new BigDecimal(totalAmount);
        if (order.getTotalAmount().compareTo(paidAmount) != 0) {
            throw new RuntimeException("订单 " + orderId + " 支付金额不匹配，应付: " + order.getTotalAmount() + ", 实付: " + totalAmount);
        }

        // 4. 更新订单状态为 "SUCCESS"
        order.setStatus("SUCCESS");
        orderRepository.save(order);
        System.out.println("我们是冠军!");
        // 5. 扣减实际库存，并减少冻结库存
        Map<Integer, Integer> lockedProducts = orderProductLocks.get(orderId);
        if (lockedProducts != null) {
            for (Map.Entry<Integer, Integer> entry : lockedProducts.entrySet()) {
                Integer productId = entry.getKey();
                Integer quantity = entry.getValue();
                Stockpile stockpile = stockpileRepository.findByProductId(productId);
                if (stockpile == null || stockpile.getAmount() < quantity) {
                    throw new RuntimeException("订单 " + orderId + " 商品 " + productId + " 库存不足（支付时）");
                }
                stockpile.setAmount(stockpile.getAmount() - quantity);
                stockpile.setFrozen(Math.max(0, stockpile.getFrozen() - quantity)); // 减少冻结库存
                stockpileRepository.save(stockpile);
                System.out.println("订单" + orderId + "支付成功，扣减商品 " + productId + " 库存 " + quantity + "，并释放锁定");
            }
            orderProductLocks.remove(orderId);
        }

        // 6. 删除 CartOrderRelation和Cart
        List<CartOrderRelation> cartOrderRelations = cartOrderRelationRepository.findByOrder(order); // 根据order查询关联关系
        if (cartOrderRelations != null && !cartOrderRelations.isEmpty()) {
            for (CartOrderRelation relation : new ArrayList<>(cartOrderRelations)) {
                cartOrderRelationRepository.delete(relation); // 删除关联关系
                System.out.println("删除关系成功!");
            }
            for (CartOrderRelation relation : cartOrderRelations) {
                cartRepository.delete(relation.getCart()); //删除购物车中的商品
            }
        }
    }

    @Override
    public void closeOrder(String orderId) {
        try {
            Integer orderIdInt = Integer.parseInt(orderId);
            Order order = orderRepository.findById(orderIdInt).orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
            if (!"SUCCESS".equals(order.getStatus()) && !"CLOSED".equals(order.getStatus())) {
                // 如果订单不是成功或已关闭状态，释放其锁定的库存
                Map<Integer, Integer> lockedProducts = orderProductLocks.get(orderId);
                if (lockedProducts != null) {
                    for (Map.Entry<Integer, Integer> entry : lockedProducts.entrySet()) {
                        Integer productId = entry.getKey();
                        Integer quantity = entry.getValue();
                        Stockpile stockpile = stockpileRepository.findByProductId(productId);
                        if (stockpile != null) {
                            stockpile.setFrozen(Math.max(0, stockpile.getFrozen() - quantity)); // 释放冻结库存
                            stockpileRepository.save(stockpile);
                            System.out.println("订单 " + orderId + " 关闭，释放商品 " + productId + " 的锁定库存 " + quantity);
                        }
                    }
                    orderProductLocks.remove(orderId);
                }
                order.setStatus("CLOSED");
                orderRepository.save(order);
                System.out.println("订单 " + orderId + " 状态更新为 CLOSED");
            } else {
                System.out.println("订单 " + orderId + " 状态为 " + order.getStatus() + "，无需关闭或释放库存");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid order ID: " + orderId);
        }
    }

    @Override
    public OrderResponse getOrders(String token) {
        // 解析 token 获取用户 ID
        Integer userId = tokenUtil.getAccount(token).getId();
        if (userId == null) {
            throw new IllegalArgumentException("无效的令牌");
        }

        // 根据 userId 查找用户的所有订单
        List<Order> orderList = orderRepository.findByUserId(userId);
        if (orderList == null || orderList.isEmpty()) {
            throw new IllegalArgumentException("没有找到订单");
        }

        // 将订单转换为 OrderVO
        List<OrderAllResponse> orderAllResponseList = new ArrayList<>();
        for (Order order : orderList) {
            OrderAllResponse orderAllResponse = new OrderAllResponse();
            orderAllResponse.setOrderId(order.getOrderId());
            orderAllResponse.setTotalAmount(order.getTotalAmount());
            orderAllResponse.setPaymentMethod(order.getPayment_method());
            orderAllResponse.setStatus(order.getStatus());
            orderAllResponse.setCreateTime(DateUtil.formatDate(order.getCreateTime()));
            orderAllResponseList.add(orderAllResponse);
        }
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrders(orderAllResponseList);
        return orderResponse;
    }
}