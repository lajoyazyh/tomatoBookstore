package com.example.tomatomall.po;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_coupons")
public class UserCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_coupon_id")
    private Integer userCouponId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "coupon_id", nullable = false)
    private Integer couponId;

    @Column(name = "receive_time", nullable = false)
    private LocalDateTime receiveTime = LocalDateTime.now();

    @Column(name = "used_time")
    private LocalDateTime usedTime;

    @Column(name = "status", nullable = false, columnDefinition = "varchar(20) default 'UNUSED'")
    private String status = "UNUSED";

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "coupon_id", referencedColumnName = "coupon_id", insertable = false, updatable = false)
    private Coupon coupon;
}