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

    @Basic
    @Column(name = "product_title")
    private String productTitle;//商品名

    @Basic
    @Column(name = "price")
    private BigDecimal price;//商品价格

    @Basic
    @Column(name = "cover")
    private String cover;//商品封面url

    public CollectVO toVO(){
        CollectVO collectVO=new CollectVO();
        collectVO.setId(this.id);
        collectVO.setUserId(this.userId);
        collectVO.setProductId(this.productId);
        collectVO.setProductTitle(this.productTitle);
        collectVO.setPrice(this.price);
        collectVO.setCover(this.cover);
        return collectVO;
    }
}
