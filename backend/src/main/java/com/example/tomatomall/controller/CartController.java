package com.example.tomatomall.controller;

import com.example.tomatomall.enums.RoleEnum;
import com.example.tomatomall.po.Account;
import com.example.tomatomall.po.Cart;
import com.example.tomatomall.po.Order;
import com.example.tomatomall.repository.CartRepository;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.service.BlockService;
import com.example.tomatomall.service.CartService;
import com.example.tomatomall.service.OrderService;
import com.example.tomatomall.service.ProductService;
import com.example.tomatomall.vo.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import com.example.tomatomall.util.TokenUtil;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Resource
    CartService cartService;

    @Resource
    BlockService blockService;

    @Resource
    private OrderService orderService;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    private TokenUtil tokenUtil;

    //  内部类，用于封装 /checkout 请求的参数
    @Setter
    @Getter
    static class OrderRequest {
        private List<Integer> cartItemIds;
        private ShippingAddress shippingAddress;
        private String payment_method;
        private Integer couponId; // 新增字段：优惠券 ID
    }

    /**
     * 加入商品到购物车
     */
    @PostMapping()
    public Response addProduct(@RequestHeader("token") String token, @RequestBody addProductPara para) {
        try {
            CartProductResponse res = cartService.addProduct(token, para.getProductId(), para.getQuantity());
            return Response.buildSuccess(res);
        } catch (IllegalArgumentException e) {
            return Response.buildFailure("400", e.getMessage());
        } catch (Exception e) {
            return Response.buildFailure("500", "服务器错误");
        }
    }

    /**
     * 删除购物车商品
     */
    @DeleteMapping("/{cartItemId}")
    public Response deleteProduct(@RequestHeader("token") String token, @PathVariable Integer cartItemId) {
        String res = cartService.deleteProduct(cartItemId);
        if(res.equals("删除成功")) {
            return Response.buildSuccess(res);
        }else if (res.equals("购物车商品不存在")) {
            return Response.buildFailure("400", res);
        }
        return Response.buildFailure("400", "你的后端方法实现错了，再回去沉淀沉淀！");
    }

    /**
     * 修改购物车商品数量
     */
    @PatchMapping("/{cartItemId}")
    public Response changeProductAmount(@RequestHeader("token") String token, @PathVariable Integer cartItemId, @RequestParam Integer quantity) {
        String res = cartService.changeProductAmount(cartItemId, quantity);
        if(res.equals("修改数量成功")) {
            return Response.buildSuccess(res);
        }else if (res.equals("购物车商品不存在")) {
            return Response.buildFailure("400", res);
        } else if (res.equals("超出库存数量")) {
            return Response.buildFailure("400", res);
        }
        return Response.buildFailure("400", "你的后端方法实现错了，再回去沉淀沉淀！");
    }

    /**
     * 获取购物车商品列表
     */
    @GetMapping()
    public Response getCartAll(@RequestHeader("token") String token) {
        try {
            CartAllResponse res = cartService.getCartAll(token);
            return Response.buildSuccess(res);
        } catch (IllegalArgumentException e) {
            return Response.buildFailure("400", e.getMessage());
        } catch (Exception e) {
            return Response.buildFailure("500", "服务器错误");
        }
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
        Account thisAccount=tokenUtil.getAccount(token);
        if(blockService.judgeBlock(thisAccount.getId())){
            return Response.buildFailure("400","操作失败，请联系管理员");
        }
        String username = thisAccount.getUsername();
        List<Integer> cartItemIds = orderRequest.getCartItemIds();
        ShippingAddress shippingAddress = orderRequest.getShippingAddress();
        String payment_method = orderRequest.getPayment_method();
        Integer couponId = orderRequest.getCouponId(); // 获取优惠券 ID

        Order order = orderService.createOrder(username, cartItemIds, shippingAddress, payment_method, couponId);

        //  构建符合期望返回结构的 Map
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", order.getOrderId());
        result.put("username", username);
        result.put("totalAmount", order.getTotalAmount());
        result.put("paymentMethod", order.getPayment_method());
        result.put("createTime", order.getCreateTime());
        result.put("status", order.getStatus());
        if (order.getCouponId() != null) {
            result.put("couponId", order.getCouponId()); // 返回使用的优惠券 ID
        }

        return Response.buildSuccess(result);
    }
}