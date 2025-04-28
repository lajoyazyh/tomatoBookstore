package com.example.tomatomall.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class OrderAllResponse {
    private Integer orderId;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String status;
    private String createTime;
}
