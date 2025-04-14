package com.example.tomatomall.vo;

import com.example.tomatomall.po.Account;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.po.Cart;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class CartVO {

    private Integer cartItemId;
    private Integer userId;
    private Integer productId;
    private Integer quantity;

    public Cart toPO(){
        Cart cart = new Cart();
        cart.setCartItemId(this.cartItemId);
        cart.setQuantity(this.quantity);

        if(this.userId != null) {
            Account account = new Account();
            account.setId(this.userId);
            cart.setAccount(account);
        }

        if(this.productId != null) {
            Product product = new Product();
            product.setId(this.productId);
            cart.setProduct(product);
        }

        return cart;
    }
}
