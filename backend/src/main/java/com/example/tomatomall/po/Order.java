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
    private String orderId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "status")
    private String status;

    @Column(name = "create_time")
    private Date createTime;

    @Transient //  不映射到数据库列
    private ShippingAddress shippingAddress;  //  Order 包含 ShippingAddress

    //  转换为 OrderVO
    public OrderVO toVO() {
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderId(this.orderId);
        orderVO.setUserId(this.userId);
        orderVO.setTotalAmount(this.totalAmount);
        orderVO.setPaymentMethod(this.paymentMethod);
        orderVO.setStatus(this.status);
        orderVO.setCreateTime(this.createTime);
        orderVO.setShippingAddress(this.shippingAddress); // 设置 ShippingAddress
        return orderVO;
    }
}