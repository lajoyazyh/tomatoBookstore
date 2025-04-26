package com.example.tomatomall.vo;

import com.example.tomatomall.po.Advertisement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdvertisementVO {
    private Integer id;
    private String title;
    private String content;
    private String imgUrl;
    private Integer productId;

    public Advertisement toPO(){
        Advertisement advertisement=new Advertisement();
        advertisement.setId(this.id);
        advertisement.setTitle(this.title);
        advertisement.setContent(this.content);
        advertisement.setImageUrl(this.imgUrl);
        advertisement.setProductId(this.productId);
        return advertisement;
    }
}
