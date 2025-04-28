package com.example.tomatomall.po;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.example.tomatomall.vo.AdvertisementVO;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Advertisement {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;//广告id

    @Basic
    @Column(name = "title",nullable = false)
    private String title;//广告标题,不允许为空

    @Basic
    @Column(name = "content")
    private String content;//广告内容

    @Basic
    @Column(name = "image_url")
    private String imageUrl;//广告图片url

    @Basic
    @Column(name = "product_id",nullable = false)
    private Integer productId;//所属商品id，不允许为空

    public AdvertisementVO toVO(){
        AdvertisementVO advertisementVO=new AdvertisementVO();
        advertisementVO.setId(this.id);
        advertisementVO.setTitle(this.title);
        advertisementVO.setContent(this.content);
        advertisementVO.setImgUrl(this.imageUrl);
        advertisementVO.setProductId(this.productId);
        return advertisementVO;
    }
}
