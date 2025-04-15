package com.example.tomatomall.repository;

import com.example.tomatomall.po.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {

    @Query("SELECT c FROM Cart c WHERE c.account.id = ?1 AND c.product.id = ?2")
    Cart findByUserIdAndProductId(Integer userId, Integer productId);

    List<Cart> findByUserId(Integer userId);
}
