package com.example.tomatomall.vo;

import com.example.tomatomall.po.Collect;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.criteria.CriteriaBuilder;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
public class CollectVO {
    private Integer id;
    private Integer userId;
    private Integer productId;
    private String productTitle;
    private BigDecimal price;
    private String cover;
    public Collect toPO(){
        Collect collect=new Collect();
        collect.setId(this.id);
        collect.setUserId(this.userId);
        collect.setProductId(this.productId);
        collect.setProductTitle(this.productTitle);
        collect.setPrice(this.price);
        collect.setCover(this.cover);
        return collect;
    }
}
