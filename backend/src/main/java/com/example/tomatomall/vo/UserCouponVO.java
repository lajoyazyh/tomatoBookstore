package com.example.tomatomall.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UserCouponVO {
    private Integer userCouponId;
    private Integer userId;
    private Integer couponId;
    private LocalDateTime receiveTime;
    private LocalDateTime usedTime;
    private String status;
    private CouponVO coupon; // 包含优惠券的详细信息
}