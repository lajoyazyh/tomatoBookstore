package com.example.tomatomall.controller;

import com.example.tomatomall.po.Cart;
import com.example.tomatomall.repository.CartRepository;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.service.CartService;
import com.example.tomatomall.service.ProductService;
import com.example.tomatomall.vo.CartProductResponse;
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
    public Response addProduct(@RequestHeader("token") String token, @RequestParam Integer productId, @RequestParam Integer quantity) {
        try {
            CartProductResponse res = cartService.addProduct(token, productId, quantity);
            return Response.buildSuccess(res);
        } catch (IllegalArgumentException e) {
            return Response.buildFailure("400", e.getMessage());
        } catch (Exception e) {
            return Response.buildFailure("500", "服务器错误");
        }
    }


}
