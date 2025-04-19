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
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2, columnDefinition = "DECIMAL(10,2) COMMENT '订单总金额'")
    private BigDecimal totalAmount;

    @Basic
    @Column(name = "payment_method", nullable = false, length = 50, columnDefinition = "VARCHAR(50) COMMENT '支付方式'")
    private String payment_method;

    @Basic
    @Column(name = "status", nullable = false, length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'PENDING' COMMENT '订单支付状态'")
    private String status = "PENDING"; // 订单支付状态（PENDING, SUCCESS, FAILED, TIMEOUT）

    @Basic
    @Column(name = "create_time", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '订单创建时间'")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Transient // 不映射到数据库列
    private ShippingAddress shippingAddress;  // 虚拟字段：发货地址

    // 转换为 VO（视图对象）
    public OrderVO toVO() {
        OrderVO orderVO = new OrderVO();
        orderVO.setUserId(this.account.getId()); // 提取 account 的主键 ID
        orderVO.setTotalAmount(this.totalAmount);
        orderVO.setPaymentMethod(this.payment_method);
        orderVO.setStatus(this.status);
        orderVO.setCreateTime(this.createTime);
        orderVO.setShippingAddress(this.shippingAddress); // 附加虚拟字段
        return orderVO;
    }


}
