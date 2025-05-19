package com.example.tomatomall.vo;

import com.example.tomatomall.po.Order;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class OrderVO {
    private Integer orderId;
    private Integer userId;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String status;
    private Date createTime;
    private Boolean commented = false; // 是否已评论
    private ShippingAddress shippingAddress;
    private Integer couponId; // 使用的优惠券 ID

    public Order toPO(){
        Order order=new Order();
        order.setOrderId(this.orderId);
        order.setTotalAmount(this.totalAmount);
        order.setPayment_method(this.paymentMethod);
        order.setStatus(this.status);
        order.setCreateTime(this.createTime);
        order.setCommented(this.commented);
        order.setShippingAddress(this.shippingAddress);
        order.setCouponId(this.couponId); // 设置优惠券 ID
        return order;
    }

}
