package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.service.ProductService;
import com.example.tomatomall.util.SecurityUtil;
import com.example.tomatomall.vo.ProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    SecurityUtil securityUtil;

    @Override
    public List<ProductVO> getAllProducts() {
        List<Product> allProducts=productRepository.findAll();
        List<ProductVO> allProductVOs=new ArrayList<>();
        for(Product product:allProducts){
            ProductVO productVO=product.toVO();
            allProductVOs.add(productVO);
        }
        return allProductVOs;
    }

    @Override
    public ProductVO getProduct(String id){
        Product thisProduct=productRepository.findById(id);
        return thisProduct.toVO();
    }

    @Override
    public String updateInformation(ProductVO productVO){
        Product thisProduct=productRepository.findById(productVO.getId());
        if(productVO.getTitle()!=null){
            thisProduct.setTitle(productVO.getTitle());
        }
        if(productVO.getPrice()!=null){
            if(productVO.getPrice().compareTo(BigDecimal.ZERO)<0){
                return "价格不能小于0";
            }
            thisProduct.setPrice(productVO.getPrice());
        }
        if(productVO.getRate()!=null){
            if(productVO.getRate()<0||productVO.getRate()>10){
                return "商品评分区间为0-10";
            }
            thisProduct.setRate(productVO.getRate());
        }
        if(productVO.getDescription()!=null){
            thisProduct.setDescription(productVO.getDescription());
        }
        if(productVO.getCover()!=null){
            thisProduct.setCover(productVO.getCover());
        }
        if(productVO.getDetail()!=null){
            thisProduct.setDetail(productVO.getDetail());
        }
        if(productVO.getSpecificationVOs()!=null){
            thisProduct.addSpecifications(productVO.getSpecificationVOs());
        }
        return "更新成功";
    }
}