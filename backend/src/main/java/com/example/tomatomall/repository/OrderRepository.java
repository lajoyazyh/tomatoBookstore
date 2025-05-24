package com.example.tomatomall.repository;

import com.example.tomatomall.po.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    Order findByOrderId(Integer orderId);

    @Query("SELECT o FROM Order o WHERE o.account.id = ?1")
    List<Order> findByUserId(Integer userId);

    List<Order> findByAccountIdAndStatus(Integer accountId, String status);
}