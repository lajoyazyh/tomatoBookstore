package com.example.tomatomall.service;

import com.example.tomatomall.po.Order;
import com.example.tomatomall.po.ShippingAddress;
import com.example.tomatomall.vo.OrderVO;

import java.util.List;

public interface OrderService {
    Order createOrder(String username, List<Integer> cartItemIds, ShippingAddress shippingAddress, String paymentMethod);

    OrderVO getOrderById(Integer id);
}