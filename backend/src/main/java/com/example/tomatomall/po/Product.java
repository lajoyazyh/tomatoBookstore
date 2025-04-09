package com.example.tomatomall.po;

import com.example.tomatomall.vo.SpecificationVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.example.tomatomall.vo.ProductVO;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Product {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "title")
    private String title;//商品名称

    @Basic
    @Column(name = "price")
    private BigDecimal price;//商品价格

    @Basic
    @Column(name = "rate")
    private Double rate;//商品评分

    @Basic
    @Column(name = "description")
    private String description;//商品描述

    @Basic
    @Column(name = "cover")
    private String cover;//商品封面url

    @Basic
    @Column(name = "detail")
    private String detail;//商品详细说明

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Specification> specifications; // 商品规格

    public ProductVO toVO(){
        ProductVO productVO=new ProductVO();
        productVO.setId(this.id);
        productVO.setTitle(this.title);
        productVO.setPrice(this.price);
        productVO.setRate(this.rate);
        productVO.setDescription(this.description);
        productVO.setCover(this.cover);
        productVO.setDetail(this.detail);

        List<SpecificationVO> specificationVOs = getSpecificationVOS();
        productVO.setSpecificationVOs(specificationVOs);
        return productVO;
    }

    private List<SpecificationVO> getSpecificationVOS() {
        List<SpecificationVO> specificationVOs = new ArrayList<>();
        if (this.specifications != null) {
            for (Specification specification : this.specifications) {
                SpecificationVO specificationVO = new SpecificationVO();
                specificationVO.setId(specification.getId());
                specificationVO.setItem(specification.getItem());
                specificationVO.setValue(specification.getValue());
                specificationVO.setProductId(specification.getProductId());
                specificationVOs.add(specificationVO);
            }
        }
        return specificationVOs;
    }

    public void addSpecifications(List<SpecificationVO> specificationVOs){
        if (specificationVOs != null) {
            for (SpecificationVO specificationVO : specificationVOs) {
                Specification specification = new Specification();
                specification.setId(specificationVO.getId());
                specification.setItem(specificationVO.getItem());
                specification.setValue(specificationVO.getValue());
                specification.setProductId(specificationVO.getProductId());
                this.specifications.add(specification);
            }
        }
        return;
    }
}
