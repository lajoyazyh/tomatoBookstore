package com.example.tomatomall.controller;

import com.example.tomatomall.po.Comment;
import com.example.tomatomall.service.CommentService;
import com.example.tomatomall.vo.CommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.tomatomall.vo.Response;
import java.util.List;
import java.util.Optional;
import javax.annotation.Resource;
import com.example.tomatomall.util.TokenUtil;
import com.example.tomatomall.repository.CommentRepository;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.po.Product;


/**
 * @Author: ZhuYehang
 * @Date: 2025/5/14
 */

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Resource
    CommentService commentService;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    private TokenUtil tokenUtil;

    //获取某个商品的评论列表
    @GetMapping()
    public Response getComments(@RequestParam(value = "productId") Integer productId) {
        List<CommentVO> comments = commentService.getCommentsByProductId(productId);
        return Response.buildSuccess(comments);
    }

    //删除评论
    @DeleteMapping("/{id}")
    public Response deleteComment(@PathVariable(value = "id") Integer id) {
        Comment comment = commentRepository.findById(id).get();
        if (comment != null) {
            commentService.deleteComment(id);
            return Response.buildSuccess("删除成功");
        } else {
            return Response.buildFailure("400", "评论不存在");
        }
    }

    //某个用户为某个商品添加评论
    @PostMapping()
    public Response addComment(@RequestBody CommentVO commentVO,
                               @RequestParam (value = "productId") Integer productId,
                               @RequestParam (value = "userId") Integer userId) {
        Optional<Product> productOptional = productRepository.findById(productId);

        if (productOptional.isPresent()) {
            commentService.addComment(commentVO, productId, userId);
            return Response.buildSuccess("评论成功");
        } else {
            return Response.buildFailure("400", "商品不存在");
        }
    }
}
