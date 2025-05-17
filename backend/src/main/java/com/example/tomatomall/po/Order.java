package com.example.tomatomall.po;

import com.example.tomatomall.vo.OrderVO;
import com.example.tomatomall.vo.ShippingAddress;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private Account account; // 关联用户表（users 表）

    @Basic
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Basic
    @Column(name = "payment_method", nullable = false, length = 50)
    private String payment_method;

    @Basic
    @Column(name = "status", nullable = false, length = 20)
    private String status = "PENDING"; // 订单支付状态（PENDING, SUCCESS, FAILED, TIMEOUT）

    @Basic
    @Column(name = "create_time", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Transient // 不映射到数据库列
    private ShippingAddress shippingAddress;  // 虚拟字段：发货地址

    // 新增字段：使用的优惠券 ID
    @Column(name = "coupon_id")
    private Integer couponId;

    // 新增关联：使用的优惠券
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", referencedColumnName = "coupon_id", insertable = false, updatable = false)
    private Coupon coupon;

    // 转换为 VO（视图对象）
    public OrderVO toVO() {
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderId(this.orderId);
        orderVO.setUserId(this.account.getId());
        orderVO.setTotalAmount(this.totalAmount);
        orderVO.setPaymentMethod(this.payment_method);
        orderVO.setStatus(this.status);
        orderVO.setCreateTime(this.createTime);
        orderVO.setShippingAddress(this.shippingAddress); // 附加虚拟字段
        orderVO.setCouponId(this.couponId); // 添加优惠券 ID
        if (this.coupon != null) {
            orderVO.setCouponVO(this.coupon.toVO()); // 如果需要，可以包含优惠券的详细信息
            orderVO.setUsedCoupon(true); // 标记使用了优惠券
        } else {
            orderVO.setUsedCoupon(false); // 标记未使用优惠券
            orderVO.setCouponVO(null);
        }
        return orderVO;
    }
}