package com.example.tomatomall.po;

import com.example.tomatomall.vo.ProductVO;
import com.example.tomatomall.vo.CartVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "carts")
public class Cart {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "cart_item_id", columnDefinition = "COMMENT '购物车商品ID'")
    private Integer cartItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, columnDefinition = "COMMENT '用户id，关联用户表'")
    private Account account; // 关联用户表

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false, columnDefinition = "COMMENT '商品id，关联商品表'")
    private Product product; // 关联商品表

    @Basic
    @Column(name = "quantity", nullable = false, columnDefinition = "COMMENT '商品数量，默认为1'")
    private Integer quantity = 1; // 商品数量，默认为1

    public CartVO toVO() {
        CartVO cartVO = new CartVO();
        cartVO.setCartItemId(this.cartItemId);
        cartVO.setQuantity(this.quantity);
        cartVO.setUserId(this.account.getId());
        cartVO.setProductId(this.product.getId());

        return cartVO;
    }

}