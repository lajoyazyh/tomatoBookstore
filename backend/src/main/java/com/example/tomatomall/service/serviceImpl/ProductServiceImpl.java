package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.service.ProductService;
import com.example.tomatomall.util.SecurityUtil;
import com.example.tomatomall.util.TokenUtil;
import com.example.tomatomall.vo.ProductVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @Author: ZhuYehang
 * @Date: 2025/4/9
 *
 * 商品增删改查功能实现
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    TokenUtil tokenUtil;

    @Autowired
    SecurityUtil securityUtil;

    @Override
    public String register(ProductVO productVO) {
        Product product = productRepository.findByTitle(productVO.getTitle());
        if(product != null) {
            return "商品名已存在";
        }

        Product newProduct = productVO.toPO();

        productRepository.save(newProduct);
        return "创建成功";
    }

    @Override
    public String delete(Integer id) {
        Optional<Product> product = productRepository.findById(id);
        if(product == null) {
            return "商品不存在";
        }

        productRepository.delete(product);
        return "删除成功";

    }
}
