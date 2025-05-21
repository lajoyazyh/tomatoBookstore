package com.example.tomatomall.repository;

import com.example.tomatomall.po.Collect;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
public interface CollectRepository extends JpaRepository<Collect, Integer> {
    Optional<Collect> findByUserIdAndProductId(Integer userId, Integer productId);

    List<Collect> findByUserId(Integer userId);
}
