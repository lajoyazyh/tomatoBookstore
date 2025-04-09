package com.example.tomatomall.vo;

import com.example.tomatomall.po.Product;
import com.example.tomatomall.po.Specification;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor

public class ProductVO {
    private Integer id;

    private String title;//商品名称

    private BigDecimal price;//商品价格

    private Double rate;//商品评分

    private String description;//商品描述

    private String cover;//商品封面url

    private String detail;//商品详细说明

    private List<SpecificationVO> specificationVOs;

    public Product toPO(){
        Product product=new Product();
        product.setId(this.id);
        product.setTitle(this.title);
        product.setPrice(this.price);
        product.setRate(this.rate);
        product.setDescription(this.description);
        product.setCover(this.cover);
        product.setDetail(this.detail);

        List<Specification> specifications = getSpecifications();
        product.setSpecifications(specifications);

        return product;
    }

    private List<Specification> getSpecifications() {
        List<Specification> specifications = new ArrayList<>();
        if (this.specificationVOs != null) {
            for (SpecificationVO specificationVO : this.specificationVOs) {
                Specification specification = new Specification();
                specification.setId(specificationVO.getId());
                specification.setItem(specificationVO.getItem());
                specification.setValue(specificationVO.getValue());
                specification.setProductId(specificationVO.getProductId());
                specifications.add(specification);
            }
        }
        return specifications;
    }
}
