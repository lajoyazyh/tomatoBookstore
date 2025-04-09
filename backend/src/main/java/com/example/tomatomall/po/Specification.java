package com.example.tomatomall.po;

import com.example.tomatomall.vo.SpecificationVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Specification {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;

    @Basic
    @Column(name = "item")
    private String item;//规格名

    @Basic
    @Column(name = "value")
    private String value;//规格内容

    @Basic
    @Column(name = "productId")
    private Integer productId; // 所属商品id

    public SpecificationVO toVO() {
        SpecificationVO specificationVO = new SpecificationVO();
        specificationVO.setId(this.id);
        specificationVO.setItem(this.item);
        specificationVO.setValue(this.value);
        specificationVO.setProductId(this.productId);
        return specificationVO;
    }
}
