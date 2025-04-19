package com.example.tomatomall.vo;

import com.example.tomatomall.po.Cart;
import com.example.tomatomall.po.CartOrderRelation;
import com.example.tomatomall.po.Order;

public class CartOrderRelationVO {
    Integer id;
    Integer orderId;
    Integer cartitemId;
    public CartOrderRelation toVO() {
        CartOrderRelation cartOrderRelation = new CartOrderRelation();
        cartOrderRelation.setId(this.id);
        if(this.orderId != null){
            Order order = new Order();
            order.setOrderId(this.orderId);
            cartOrderRelation.setOrder(order);
        }
        if(this.cartitemId != null){
            Cart cart = new Cart();
            cart.setCartItemId(this.cartitemId);
            cartOrderRelation.setCart(cart);
        }
        return cartOrderRelation;
    }
}
