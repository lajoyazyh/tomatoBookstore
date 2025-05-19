package com.example.tomatomall.repository;

import com.example.tomatomall.po.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Integer> {

    List<UserCoupon> findByUserId(Integer userId);

    Optional<UserCoupon> findByUserIdAndCouponId(Integer userId, Integer couponId);

    List<UserCoupon> findByUserIdAndStatus(Integer userId, String status);

    Optional<UserCoupon> findByUserIdAndCouponIdAndStatus(Integer userId, Integer couponId, String unused);
}