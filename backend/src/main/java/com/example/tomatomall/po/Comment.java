package com.example.tomatomall.po;

import com.example.tomatomall.vo.CommentVO;
import  lombok.Getter;
import  lombok.NoArgsConstructor;
import  lombok.Setter;

import javax.persistence.*;

/**
 * @Author: ZhuYehang
 * @Date: 2025/5/14
 */

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Comment {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;

    @Basic
    @Column(name = "rating")
    private Double rating;//评分

    @Basic
    @Column(name = "content")
    private String content;//评论内容

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product; // 关联的商品

    public CommentVO toVO() {
        CommentVO commentVO = new CommentVO();
        commentVO.setId(this.id);
        commentVO.setRating(this.rating);
        commentVO.setContent(this.content);
        commentVO.setProductId(this.product.getId());
        return commentVO;
    }

    public Integer getProductId() {
        if (this.product != null) {
            return this.product.getId();
        }
        return null;
    }

}
