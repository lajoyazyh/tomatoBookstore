package com.example.tomatomall.repository;

import com.example.tomatomall.po.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findByProductId(Integer id);

    void deleteByProductId(Integer productId);

    Optional<Comment> findById(Integer id);

    void deleteById(Integer id);
}
