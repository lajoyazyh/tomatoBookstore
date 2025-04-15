package com.example.tomatomall.vo;

import java.math.BigDecimal;
import java.util.List;

import com.example.tomatomall.po.Account;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.po.Cart;
import com.example.tomatomall.vo.CartProductResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class CartAllResponse {
    List<CartProductResponse> items;
    Integer total;
    Double totalAmount;

    //Getters and Setters


    public List<CartProductResponse> getItems() {
        return items;
    }

    public Integer getTotal() {
        return total;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setItems(List<CartProductResponse> items) {
        this.items = items;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
