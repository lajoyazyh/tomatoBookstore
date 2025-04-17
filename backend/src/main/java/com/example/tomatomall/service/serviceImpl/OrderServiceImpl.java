// OrderServiceImpl.java
package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.po.Account;
import com.example.tomatomall.po.Cart;
import com.example.tomatomall.po.Order;
import com.example.tomatomall.po.Stockpile;
import com.example.tomatomall.repository.*;
import com.example.tomatomall.service.OrderService;
import com.example.tomatomall.util.TokenUtil;
import com.example.tomatomall.vo.ShippingAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

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
    private TokenUtil tokenUtil;


    @Override
    @Transactional
    public Order createOrder(String username, List<Integer> cartItemIds, ShippingAddress shippingAddress, String paymentMethod) {
        // 1.  验证cartItemIds是否为空
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            throw new IllegalArgumentException("cartItemIds不能为空");
        }
        // 2. 获取用户购物车商品，并验证商品是否存在
        List<Cart> carts = cartRepository.findAllById(cartItemIds);
        if (carts.size() != cartItemIds.size()) {
            throw new IllegalArgumentException("购物车商品不存在");
        }

        // 3. 验证用户是否一致, 并获取用户ID
        Integer userId = null;
        for (Cart cart : carts) {
            if (!cart.getAccount().getUsername().equals(username)) {
                throw new IllegalArgumentException("用户不一致");
            }
            // 从token中获取的username 和 cart 中的username 是一致的，这里只需要获取一次
            if (userId == null) {
                userId = cart.getAccount().getId();
            }
        }

        // 4. 验证库存，计算订单总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Cart cart : carts) {
            Stockpile stockpile = stockpileRepository.findByProductId(cart.getProduct().getId());
            if (stockpile == null || stockpile.getAmount() < cart.getQuantity()) {
                throw new IllegalArgumentException("商品" + cart.getProduct().getTitle() + "库存不足");
            }
            totalAmount = totalAmount.add(cart.getProduct().getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
        }

        // 5. 创建订单
        Order order = new Order();
        order.setTotalAmount(totalAmount);
        order.setPaymentMethod(paymentMethod);
        order.setStatus("PENDING"); // 初始状态为待支付
        order.setCreateTime(new Date());

        // 6. 根据 userId 查询 Account 对象并设置到 order 中
        Account account = accountRepository.findById(userId).get();
        order.setAccount(account);

        orderRepository.save(order);

        return order;

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

//        // 5. 扣减库存并处理关联表
//        List<CartsOrdersRelation> relations = cartsOrdersRelationRepository.findByOrderId(orderId); // 查询关联表
//
//        if (relations != null && !relations.isEmpty()) {
//            for (CartsOrdersRelation relation : relations) {
//                Integer cartItemId = relation.getCartitemId();
//                //  根据 cartItemId  扣减库存
//                //  这里你需要根据你的 CartRepository 来查询到 Cart
//                //  然后获取 productId 和 quantity
//                //  以下代码仅为示例，你需要根据你的实际情况修改
//                //  ！！！  这里需要修改  ！！！
//                //  示例：
//                // Cart cart = cartRepository.findById(cartItemId).orElse(null);
//                // if (cart != null) {
//                //     Integer productId = cart.getProduct().getId();
//                //     Integer quantity = cart.getQuantity();
//                //     Stockpile stockpile = stockpileRepository.findByProductId(productId);
//                //     if (stockpile == null || stockpile.getAmount() < quantity) {
//                //         throw new RuntimeException("订单 " + orderId + " 商品 " + productId + " 库存不足");
//                //     }
//                //     stockpile.setAmount(stockpile.getAmount() - quantity);
//                //     stockpileRepository.save(stockpile);
//                // }
//                //  删除关联表中的记录
//                CartsOrdersRelationRepository.delete(relation);
//            }
//            System.out.println("成功处理订单" + orderId + "，并扣减库存和删除关联表记录");
//        } else {
//            System.out.println("订单" + orderId + "没有关联的购物车项");
//        }
    }

}