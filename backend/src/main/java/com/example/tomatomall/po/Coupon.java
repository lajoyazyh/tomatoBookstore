package com.example.tomatomall.po;

import com.example.tomatomall.vo.CouponVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "coupon")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Integer couponId;
    @Column(name = "coupon_name", nullable = false)
    private String couponName; // 优惠券名称，例如“满减券”、“折扣券”
    @Column(name = "coupon_type", nullable = false)
    private String couponType; // 优惠券类型，例如：FIXED（固定金额减免）、PERCENTAGE（百分比折扣）、THRESHOLD（满减）
    @Column(name = "discount_amount")
    private BigDecimal discountAmount; // 减免金额 (当 couponType 为 FIXED 或 THRESHOLD 时使用)
    @Column(name = "discount_percentage")
    private BigDecimal discountPercentage; // 折扣百分比 (当 couponType 为 PERCENTAGE 时使用，例如 0.9 表示九折)
    @Column(name = "min_purchase_amount")
    private BigDecimal minPurchaseAmount; // 最低消费金额 (当 couponType 为 THRESHOLD 时使用)
    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom; // 有效期开始时间
    @Column(name = "valid_until", nullable = false)
    private LocalDateTime validUntil; // 有效期结束时间
    @Column(name = "status", nullable = false, columnDefinition = "varchar(20) default 'ACTIVE'")
    private String status; // 优惠券状态：ACTIVE（可用）、INACTIVE（禁用）、EXPIRED（已过期）
    @Column(name = "description")
    private String description; // 优惠券描述

    public CouponVO toVO(){
        CouponVO couponVO = new CouponVO();
        couponVO.setCouponId(this.couponId);
        couponVO.setCouponName(this.couponName);
        couponVO.setCouponType(this.couponType);
        couponVO.setDiscountAmount(this.discountAmount);
        couponVO.setDiscountPercentage(this.discountPercentage);
        couponVO.setMinPurchaseAmount(this.minPurchaseAmount);
        couponVO.setValidFrom(this.validFrom);
        couponVO.setValidUntil(this.validUntil);
        couponVO.setStatus(this.status);
        couponVO.setDescription(this.description);
        return couponVO;
    }
}