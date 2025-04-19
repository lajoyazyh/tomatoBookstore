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
    private ShippingAddress shippingAddress;
    public Order toPO(){
        Order order=new Order();
        order.setOrderId(this.orderId);
        order.setTotalAmount(this.totalAmount);
        order.setPaymentMethod(this.paymentMethod);
        order.setStatus(this.status);
        order.setCreateTime(this.createTime);
        order.setShippingAddress(this.shippingAddress);
        return order;
    }
}
