package com.example.tomatomall.service;

import com.example.tomatomall.vo.ProductVO;

public interface ProductService {
    String register(ProductVO productVO);

    String delete(Integer id);
}
