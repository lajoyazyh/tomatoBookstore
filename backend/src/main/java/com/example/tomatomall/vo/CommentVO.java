package com.example.tomatomall.vo;

import com.example.tomatomall.po.Product;
import com.example.tomatomall.po.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class CommentVO {
    private Integer id;
    private Double rating;//评分
    private String content;//评论内容
    private Integer productId;

    public Comment toPO() {
        Comment comment = new Comment();
        comment.setId(this.id);
        comment.setRating(this.rating);
        comment.setContent(this.content);
        if (this.productId != null) {
            Product product = new Product();
            product.setId(this.productId);
            comment.setProduct(product);
        }
        return comment;
    }
}
