package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Account;
import com.example.tomatomall.po.Cart;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.repository.*;
import com.example.tomatomall.service.CartService;
import com.example.tomatomall.util.SecurityUtil;
import com.example.tomatomall.vo.CartAllResponse;
import com.example.tomatomall.vo.CartVO;
import com.example.tomatomall.vo.CartProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.tomatomall.util.TokenUtil;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @Author: ZhuYehang
 * @Date: 2025/4/14
 *
 * 购物车功能实现
 */

@Service

public class CartServiceImpl implements  CartService {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    StockpileRepository stockpileRepository;

    @Autowired
    TokenUtil tokenUtil;

    @Autowired
    SecurityUtil securityUtil;


    @Override
    public CartProductResponse addProduct(String token, Integer productId, Integer quantity) {

        // 解析 token 获取用户 ID
        Integer userId = tokenUtil.getAccount(token).getId();
        if (userId == null) {
            throw new IllegalArgumentException("无效的令牌");
        }

        // 查询商品信息
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            throw new IllegalArgumentException("商品不存在");
        }

        // 检查商品是否已经在购物车中
        Cart cartItem = cartRepository.findByUserIdAndProductId(userId, productId);
        if (cartItem != null) {
            throw new IllegalArgumentException("该商品已存在");
        }

        // 检查商品数量是否超过库存
        Integer cartProductId = product.getId();
        if(quantity > stockpileRepository.findByProductId(cartProductId).getAmount()) {
            throw new IllegalArgumentException("超出库存数量");
        }

        // 创建新的购物车条目
        cartItem = new Cart();
        cartItem.setAccount(accountRepository.findById(userId).orElse(null)); // 设置用户
        cartItem.setProduct(product); // 设置商品
        cartItem.setQuantity(quantity); // 设置数量
        cartRepository.save(cartItem);

        // 创建返回的 DTO 对象
        CartProductResponse response = new CartProductResponse();
        response.setCartItemId(cartItem.getCartItemId());
        response.setProductId(productId);
        response.setTitle(product.getTitle());
        response.setPrice(product.getPrice());
        response.setDescription(product.getDescription());
        response.setCover(product.getCover());
        response.setDetail(product.getDetail());
        response.setQuantity(quantity);

        return response;
    }

    @Override
    public String deleteProduct(Integer cartItemId) {
        Cart cartItem = cartRepository.findById(cartItemId).get();
        if(cartItem == null) {
            return "购物车商品不存在";
        }

        cartRepository.delete(cartItem);
        return "删除成功";
    }

    @Override
    public String changeProductAmount(Integer cartItemId, Integer quantity) {
        Cart cartItem = cartRepository.findById(cartItemId).get();
        if(cartItem == null) {
            return "购物车商品不存在";
        }

        Integer productId = cartItem.getProduct().getId();
        if(quantity > stockpileRepository.findByProductId(productId).getAmount()) {
            return "超出库存数量";
        }

        cartItem.setQuantity(quantity);
        cartRepository.save(cartItem);
        return "修改数量成功";
    }

    @Override
    public CartAllResponse getCartAll(String token) {

        // 解析 token 获取用户 ID
        Integer userId = tokenUtil.getAccount(token).getId();
        if (userId == null) {
            throw new IllegalArgumentException("无效的令牌");
        }
        List<Cart> CartList = cartRepository.findByUserId(userId);
        List<CartProductResponse> CartProductResList = Collections.emptyList();
        Integer total = 0;
        Double totalAmount = 0.0;

        for(Cart cartItem : CartList){
            // 创建返回的 DTO 对象
            Product product = cartItem.getProduct();
            Integer productId = product.getId();
            Integer quantity = cartItem.getQuantity();

            CartProductResponse response = new CartProductResponse();
            response.setCartItemId(cartItem.getCartItemId());
            response.setProductId(productId);
            response.setTitle(product.getTitle());
            response.setPrice(product.getPrice());
            response.setDescription(product.getDescription());
            response.setCover(product.getCover());
            response.setDetail(product.getDetail());
            response.setQuantity(quantity);

            total += 1;
            totalAmount += product.getPrice().doubleValue() * quantity.doubleValue();
            CartProductResList.add(response);
        }

        CartAllResponse cartAllResponse = null;
        cartAllResponse.setItems(CartProductResList);
        cartAllResponse.setTotal(total);
        cartAllResponse.setTotalAmount(totalAmount);

        return cartAllResponse;
    }
}
