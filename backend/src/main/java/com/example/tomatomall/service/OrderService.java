package com.example.tomatomall.service;

import com.example.tomatomall.po.Order;
import com.example.tomatomall.vo.ShippingAddress;

import java.util.List;

public interface OrderService {
    Order createOrder(String username, List<Integer> cartItemIds, ShippingAddress shippingAddress, String paymentMethod);
    // 其他订单相关方法
}