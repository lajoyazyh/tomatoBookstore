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
import com.example.tomatomall.vo.Response;
import com.example.tomatomall.repository.StockpileRepository;
import com.example.tomatomall.po.Stockpile;
import com.example.tomatomall.service.ProductService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.example.tomatomall.util.TokenUtil;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @Author: ZhuYehang
 * @Date: 2025/5/14
 */


public class CommentServiceImpl {

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

    List<CommentVO> getCommentsByProductId(Integer productId) {
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

    String deleteComment(Integer id) {
        Comment comment = commentRepository.findById(id).get();
        if (comment != null) {
            commentRepository.deleteById(id);
            return "删除成功";
        } else {
            return "评论不存在";
        }
    }

    void addComment(CommentVO commentVO, Integer productId, Integer userId) {
        Comment comment = new Comment();
        Product product = productRepository.findById(productId).get();
        comment.setContent(commentVO.getContent());
        comment.setRating(commentVO.getRating());
        commentRepository.save(comment);
    }
}
