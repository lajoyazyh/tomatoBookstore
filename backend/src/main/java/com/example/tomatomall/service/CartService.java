package com.example.tomatomall.service;

import com.example.tomatomall.vo.CartProductResponse;

public interface CartService {
    CartProductResponse addProduct(String token, Integer productId, Integer quantity);

    String deleteProduct(Integer cartItemId);
}
