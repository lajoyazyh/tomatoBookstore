package com.example.tomatomall.service;

import com.example.tomatomall.po.Order;
import com.example.tomatomall.vo.ShippingAddress;

import java.util.List;

public interface OrderService {
    Order createOrder(String username, List<Integer> cartItemIds, ShippingAddress shippingAddress, String payment_method);
    void handlePaymentSuccess(Integer orderId, String alipayTradeNo, String totalAmount);
    void closeOrder(String orderId);
    List<Order> getOrders(String token);
}