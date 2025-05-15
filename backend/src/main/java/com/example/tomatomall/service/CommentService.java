package com.example.tomatomall.service;

import com.example.tomatomall.vo.CommentVO;


import java.util.List;

/**
 * @Author: ZhuYehang
 * @Date: 2025/5/14
 */

public interface CommentService {

    List<CommentVO> getCommentsByProductId(Integer productId);

    String deleteComment(Integer id);


    void addComment(CommentVO commentVO, Integer productId, Integer userId);
}
