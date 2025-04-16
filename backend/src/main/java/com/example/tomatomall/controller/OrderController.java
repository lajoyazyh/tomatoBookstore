package com.example.tomatomall.controller;

import com.example.tomatomall.po.Order;
import com.example.tomatomall.po.ShippingAddress;
import com.example.tomatomall.service.OrderService;
import com.example.tomatomall.util.TokenUtil;
import com.example.tomatomall.vo.OrderVO;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart") //  注意RequestMapping
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private TokenUtil tokenUtil;

    /**
     * 提交订单
     * @param token 用户token
     * @param cartItemIds 购物车商品id列表
     * @param shippingAddress 收货地址
     * @param paymentMethod 支付方式
     * @return 订单创建结果
     */
    @PostMapping("/checkout")
    public Response<OrderVO> checkout(
            @RequestHeader("token") String token,
            @RequestBody List<Integer> cartItemIds,
            @RequestBody ShippingAddress shippingAddress,
            @RequestParam("payment_method") String paymentMethod) {
        // 从token中获取用户名
        String username = tokenUtil.getAccount(token).getUsername();
        Order order = orderService.createOrder(username, cartItemIds, shippingAddress, paymentMethod);
        return Response.buildSuccess(order.toVO(order));
    }
}