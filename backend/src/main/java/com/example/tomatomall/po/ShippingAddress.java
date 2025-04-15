package com.example.tomatomall.po;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;

@Getter
@Setter
@NoArgsConstructor
@Embeddable // 标记为嵌入类
public class ShippingAddress {
    private String name;
    private String phone;
    private String postcode;
    private String address;
}