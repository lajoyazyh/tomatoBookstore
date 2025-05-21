package com.example.tomatomall.po;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.example.tomatomall.vo.CollectVO;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@Entity
public class Collect {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;

    @Basic
    @Column(name = "user_id")
    private Integer userId;//收藏所属用户

    @Basic
    @Column(name = "product_id")
    private Integer productId;//收藏商品id

    public CollectVO toVO(){
        CollectVO collectVO=new CollectVO();
        collectVO.setId(this.id);
        collectVO.setUserId(this.userId);
        collectVO.setProductId(this.productId);
        return collectVO;
    }
}
