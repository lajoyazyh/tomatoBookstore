package com.example.tomatomall.vo;
import com.example.tomatomall.po.Coupon;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CouponVO {
    private Integer couponId;
    private String couponName; // 优惠券名称，例如“满减券”、“折扣券”
    private String couponType; // 优惠券类型，例如：FIXED（固定金额减免）、PERCENTAGE（百分比折扣）、THRESHOLD（满减）
    private BigDecimal discountAmount; // 减免金额 (当 couponType 为 FIXED 或 THRESHOLD 时使用)
    private BigDecimal discountPercentage; // 折扣百分比 (当 couponType 为 PERCENTAGE 时使用，例如 0.9 表示九折)
    private BigDecimal minPurchaseAmount; // 最低消费金额 (当 couponType 为 THRESHOLD 时使用)
    private LocalDateTime validFrom; // 有效期开始时间
    private LocalDateTime validUntil; // 有效期结束时间
    private String status; // 优惠券状态：ACTIVE（可用）、INACTIVE（禁用）、EXPIRED（已过期）
    private String description; // 优惠券描述
    public Coupon toPO(){
        Coupon coupon = new Coupon();
        coupon.setCouponId(this.couponId);
        coupon.setCouponName(this.couponName);
        coupon.setCouponType(this.couponType);
        coupon.setDiscountAmount(this.discountAmount);
        coupon.setDiscountPercentage(this.discountPercentage);
        coupon.setMinPurchaseAmount(this.minPurchaseAmount);
        coupon.setValidFrom(this.validFrom);
        coupon.setValidUntil(this.validUntil);
        coupon.setStatus(this.status);
        coupon.setDescription(this.description);
        return coupon;
    }
}
