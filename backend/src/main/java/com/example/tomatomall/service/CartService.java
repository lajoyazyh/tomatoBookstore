package com.example.tomatomall.service;

import com.example.tomatomall.vo.CartAllResponse;
import com.example.tomatomall.vo.CartProductResponse;

public interface CartService {
    CartProductResponse addProduct(String token, Integer productId, Integer quantity);

    String deleteProduct(Integer cartItemId);

    String changeProductAmount(Integer cartItemId, Integer quantity);

    CartAllResponse getCartAll(String token);
}
