package com.example.tomatomall.service;

import com.example.tomatomall.po.Coupon;
import com.example.tomatomall.vo.CouponVO;

import java.math.BigDecimal;
import java.util.List;

public interface CouponService {
    CouponVO createCoupon(CouponVO couponVO);
    CouponVO getCouponById(Integer couponId);
    List<CouponVO> getAllCoupons();
    CouponVO updateCoupon(Integer couponId, CouponVO couponVO);
    boolean deleteCoupon(Integer couponId);
    CouponVO updateCouponStatus(Integer couponId, String status);
    void receiveCoupon(Integer userId, Integer couponId);
    List<CouponVO> getApplicableCouponsForUser(Integer userId, BigDecimal orderTotal);
}