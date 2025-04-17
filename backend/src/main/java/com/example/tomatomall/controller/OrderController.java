package com.example.tomatomall.controller;

import com.example.tomatomall.po.Order;
import com.example.tomatomall.service.OrderService;
import com.example.tomatomall.util.TokenUtil;
import com.example.tomatomall.vo.Response;
import com.example.tomatomall.vo.ShippingAddress;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class OrderController {

    @Resource
    private OrderService orderService;
    @Autowired
    private TokenUtil tokenUtil;

    //  内部类，用于封装 /checkout 请求的参数
    @Setter
    @Getter
    static class OrderRequest {
        private List<Integer> cartItemIds;
        private ShippingAddress shippingAddress;
        private String paymentMethod;
    }

    /**
     * 提交订单
     *
     * @param token        用户token
     * @param orderRequest 订单请求参数
     * @return 订单创建结果
     */
    @PostMapping("/checkout")
    public Response<Map<String, Object>> checkout(
            @RequestHeader("token") String token,
            @RequestBody OrderRequest orderRequest) {
        //  从token中获取用户名
        String username = tokenUtil.getAccount(token).getUsername();
        List<Integer> cartItemIds = orderRequest.getCartItemIds();
        ShippingAddress shippingAddress = orderRequest.getShippingAddress();
        String paymentMethod = orderRequest.getPaymentMethod();

        Order order = orderService.createOrder(username, cartItemIds, shippingAddress, paymentMethod);

        //  构建符合期望返回结构的 Map
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", order.getOrderId());
        result.put("username", username);
        result.put("totalAmount", order.getTotalAmount());
        result.put("paymentMethod", order.getPaymentMethod());
        result.put("createTime", order.getCreateTime());
        result.put("status", order.getStatus());

        return Response.buildSuccess(result);
    }
}