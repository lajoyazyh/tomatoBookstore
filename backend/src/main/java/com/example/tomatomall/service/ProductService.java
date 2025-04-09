package com.example.tomatomall.service;

import com.example.tomatomall.vo.ProductVO;

import java.util.List;

public interface ProductService {
    List<ProductVO> getAllProducts();

    ProductVO getProduct(Integer id);

    String updateInformation(ProductVO productVO);
  
    String register(ProductVO productVO);

    String delete(Integer id);
 
}
