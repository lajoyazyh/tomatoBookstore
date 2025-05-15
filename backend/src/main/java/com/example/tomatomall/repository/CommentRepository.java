package com.example.tomatomall.repository;

import com.example.tomatomall.po.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    // 使用 @Query 注解定义自定义查询
    @Query("SELECT c FROM Comment c WHERE c.product.id = :productId")
    List<Comment> findByProductId(@Param("productId") Integer productId);


    Optional<Comment> findById(Integer id);

    void deleteById(Integer id);
}
