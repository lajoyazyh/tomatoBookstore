package com.example.tomatomall.vo;

import com.example.tomatomall.po.Specification;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class SpecificationVO {
    private Integer id;

    private String item;//规格名

    private String value;//规格内容

    private Integer productId;//所属商品id

    public Specification toPO() {
        Specification specification = new Specification();
        specification.setId(this.id);
        specification.setItem(this.item);
        specification.setValue(this.value);
        specification.setProductId(this.productId);
        return specification;
    }
}
