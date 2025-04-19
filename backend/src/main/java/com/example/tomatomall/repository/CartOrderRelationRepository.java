package com.example.tomatomall.repository;

import com.example.tomatomall.po.CartOrderRelation;
import com.example.tomatomall.po.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface CartOrderRelationRepository extends JpaRepository<CartOrderRelation, Integer> {

    // 根据 id 查询 CartOrderRelation
    CartOrderRelation findCartOrderRelationById(Integer id);

    // 根据 Order 对象查询 CartOrderRelation 列表
    List<CartOrderRelation> findByOrder(Order order);
}
