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
    public Collect toPO(){
        Collect collect=new Collect();
        collect.setId(this.id);
        collect.setUserId(this.userId);
        collect.setProductId(this.productId);
        return collect;
    }
}
