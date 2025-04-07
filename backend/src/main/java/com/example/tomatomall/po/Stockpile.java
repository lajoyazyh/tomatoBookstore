package com.example.tomatomall.po;

import com.example.tomatomall.vo.StockpileVO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Stockpile {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private String id;

    @ManyToOne
    @JoinColumn(name = "productId", referencedColumnName = "id")
    private String productId; // 所属商品id

    @Basic
    @Column(name = "amount")
    private Integer amount;

    @Basic
    @Column(name = "frozen")
    private Integer frozen;

    public StockpileVO toVO(){
        StockpileVO stockpileVO=new StockpileVO();
        stockpileVO.setId(this.id);
        stockpileVO.setProductId(this.productId);
        stockpileVO.setAmount(this.amount);
        stockpileVO.setFrozen(this.frozen);
        return stockpileVO;
    }
}
