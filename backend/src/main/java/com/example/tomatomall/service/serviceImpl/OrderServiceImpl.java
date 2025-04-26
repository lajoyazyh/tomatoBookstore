package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.po.*;
import com.example.tomatomall.repository.*;
import com.example.tomatomall.service.OrderService;
import com.example.tomatomall.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.tomatomall.util.TokenUtil;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Account;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.repository.*;
import com.example.tomatomall.service.CartService;
import com.example.tomatomall.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.tomatomall.util.TokenUtil;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
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


    @Override
    @Transactional
    public Order createOrder(String username, List<Integer> cartItemIds, ShippingAddress shippingAddress, String payment_method) {
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
        Integer userId = null;
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        userId = account.getId();

        for (Cart cart : carts) {
            if (!cart.getAccount().getId().equals(userId)) {
                throw new IllegalArgumentException("购物车中包含其他用户的商品");
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
        order.setPayment_method(payment_method);
        order.setStatus("PENDING"); // 初始状态为待支付
        order.setCreateTime(new Date());
        order.setAccount(accountRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在")));
        orderRepository.save(order);

        // 6. 创建订单和购物车项的关联关系, 并保存
        for (Cart cart : carts) {
            CartOrderRelation cartOrderRelation = new CartOrderRelation();
            cartOrderRelation.setCart(cart);
            cartOrderRelation.setOrder(order);
            cartOrderRelationRepository.save(cartOrderRelation); // 保存关联关系
        }

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

        // 5. 扣减库存, 并删除 CartOrderRelation
        List<CartOrderRelation> cartOrderRelations = cartOrderRelationRepository.findByOrder(order); // 根据order查询关联关系
        if (cartOrderRelations != null && !cartOrderRelations.isEmpty()) {
            for (CartOrderRelation relation : cartOrderRelations) {
                Cart cart = relation.getCart();
                Product product = cart.getProduct();
                Integer quantity = cart.getQuantity();

                Stockpile stockpile = stockpileRepository.findByProductId(product.getId());
                if (stockpile == null || stockpile.getAmount() < quantity) {
                    throw new RuntimeException("订单 " + orderId + " 商品 " + product.getTitle() + " 库存不足");
                }
                stockpile.setAmount(stockpile.getAmount() - quantity);
                stockpileRepository.save(stockpile);
                cartOrderRelationRepository.delete(relation); // 删除关联关系
                System.out.println("订单" + orderId + "扣减商品" + product.getTitle() + "库存" + quantity + "成功");
                cartRepository.delete(cart); //删除购物车中的商品
            }
        }
    }

    @Override
    public void closeOrder(String orderId) {
        try {
            Integer orderIdInt = Integer.parseInt(orderId);
            Order order = orderRepository.findById(orderIdInt).orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
            order.setStatus("CLOSED");
            orderRepository.save(order);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid order ID: " + orderId);
        }
    }
    @Override
    public List<Order> getOrders(String token) {
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
        List<Order> orderVOList = new ArrayList<>();
        for (Order order : orderList) {
            OrderVO orderVO = new OrderVO();
            orderVO.setOrderId(order.getOrderId());
            orderVO.setUserId(order.getAccount().getId());
            orderVO.setTotalAmount(order.getTotalAmount());
            orderVO.setPaymentMethod(order.getPayment_method());
            orderVO.setStatus(order.getStatus());
            orderVO.setCreateTime(order.getCreateTime());
        }

        return orderVOList;
    }


}

