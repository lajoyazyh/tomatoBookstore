// Order.java
package com.example.tomatomall.po;

import com.example.tomatomall.vo.OrderVO;
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
    private Integer id;

    @Column(name = "orderId")
    private String orderId; // 订单ID

    @Column(name = "userId")
    private Integer userId; // 用户ID

    @Column(name = "total_amount")
    private BigDecimal totalAmount; // 订单总金额

    @Column(name = "payment_method")
    private String paymentMethod; // 支付方式

    @Column(name = "status")
    private String status; // 订单状态：PENDING, PAID, SHIPPED, COMPLETED, CANCELED

    @Column(name = "create_time")
    private Date createTime; // 创建时间

    @Embedded
    private ShippingAddress shippingAddress; // 收货地址
    public OrderVO toVO(Order order){
        OrderVO orderVO=new OrderVO();
        orderVO.setId(order.getId());
        orderVO.setOrderId(order.getOrderId());
        orderVO.setUserId(order.getUserId());
        orderVO.setTotalAmount(order.getTotalAmount());
        orderVO.setPaymentMethod(order.getPaymentMethod());
        orderVO.setStatus(order.getStatus());
        orderVO.setCreateTime(order.getCreateTime());
        orderVO.setShippingAddress(order.getShippingAddress());
        return orderVO;
    }
}