package com.example.tomatomall.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderResponse {
    List<OrderAllResponse> orders;
}