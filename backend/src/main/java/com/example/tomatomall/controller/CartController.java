package com.example.tomatomall.controller;

import com.example.tomatomall.po.Cart;
import com.example.tomatomall.repository.CartRepository;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.service.CartService;
import com.example.tomatomall.service.ProductService;
import com.example.tomatomall.vo.CartProductResponse;
import com.example.tomatomall.vo.CartAllResponse;
import com.example.tomatomall.vo.CartVO;
import com.example.tomatomall.vo.ProductVO;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import com.example.tomatomall.util.TokenUtil;

import javax.annotation.Resource;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Resource
    CartService cartService;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    private TokenUtil tokenUtil;

    /**
     * 加入商品到购物车
     */
    @PostMapping()
    public Response addProduct(@RequestHeader("token") String token, @RequestBody Integer productId, @RequestBody Integer quantity) {
        try {
            CartProductResponse res = cartService.addProduct(token, productId, quantity);
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
    public Response changeProductAmount(@RequestHeader("token") String token, @PathVariable Integer cartItemId, @RequestBody Integer quantity) {
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
        CartAllResponse res = cartService.getCartAll(token);


        return Response.buildFailure("400", "你的后端方法实现错了，再回去沉淀沉淀！");
    }

}
