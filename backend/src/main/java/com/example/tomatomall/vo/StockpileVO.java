package com.example.tomatomall.vo;

import com.example.tomatomall.po.Stockpile;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class StockpileVO {
    private String id;

    private String productId;//所属商品id

    private Integer amount;//商品库存

    private Integer frozen;//商品库存冻结数

    public Stockpile toPO(){
        Stockpile stockpile=new Stockpile();
        stockpile.setId(this.id);
        stockpile.setProductId(this.productId);
        stockpile.setAmount(this.amount);
        stockpile.setFrozen(this.frozen);
        return stockpile;
    }
}
