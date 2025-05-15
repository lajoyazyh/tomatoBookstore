package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.service.CommentService;
import com.example.tomatomall.util.SecurityUtil;
import com.example.tomatomall.vo.CommentVO;
import org.springframework.stereotype.Service;
import com.example.tomatomall.po.Comment;
import com.example.tomatomall.repository.CommentRepository;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.po.Product;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.tomatomall.repository.StockpileRepository;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.tomatomall.util.TokenUtil;

/**
 * @Author: ZhuYehang
 * @Date: 2025/5/14
 */

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    StockpileRepository stockpileRepository;

    @Autowired
    TokenUtil tokenUtil;

    @Autowired
    SecurityUtil  securityUtil;

    @Override
    public List<CommentVO> getCommentsByProductId(Integer productId) {
        List<Comment> comments = commentRepository.findByProductId(productId);

        List<CommentVO> commentVOs = new ArrayList<>();
        for (Comment comment : comments) {
            CommentVO commentVO = new CommentVO();
            commentVO.setId(comment.getId());
            commentVO.setProductId(comment.getProductId());
            commentVO.setContent(comment.getContent());
            commentVO.setRating(comment.getRating());
            commentVOs.add(commentVO);
        }
        return commentVOs;
    }

    @Override
    public String deleteComment(Integer id) {
        Comment comment = commentRepository.findById(id).get();
        //获取comment对应的product
        Product product = comment.getProduct();
        //获取product的rating和ratingCount
        Double rating = product.getRating();
        Integer ratingCount = product.getRatingCount();
        //重新计算rating和ratingCount
        product.setRating((rating * ratingCount - comment.getRating()) / (ratingCount - 1));
        product.setRatingCount(ratingCount - 1);

        if (comment != null) {
            commentRepository.deleteById(id);
            return "删除成功";
        } else {
            return "评论不存在";
        }
    }

    @Override
    public void addComment(CommentVO commentVO, Integer productId, Integer userId) {
        Optional<Product> productOptional = productRepository.findById(productId);

        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            Comment comment = new Comment();
            comment.setContent(commentVO.getContent());
            comment.setRating(commentVO.getRating());
            comment.setProduct(product); // 设置 product 属性
            commentRepository.save(comment);

            int ratingCount = product.getRatingCount() == null ? 0 : product.getRatingCount();
            Double rating = product.getRating() == null ? 0 : product.getRating();
            product.setRatingCount(ratingCount + 1);
            product.setRating(((rating * ratingCount + commentVO.getRating()) / (ratingCount + 1)));
            productRepository.save(product);

        } else {
            throw new IllegalArgumentException("商品不存在");
        }
    }
}
