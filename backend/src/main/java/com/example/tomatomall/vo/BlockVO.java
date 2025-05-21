package com.example.tomatomall.vo;

import com.example.tomatomall.po.Block;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.criteria.CriteriaBuilder;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
public class BlockVO {
    private Integer id;
    private Integer userId;
    private String userName;
    public Block toPO(){
        Block block=new Block();
        block.setId(this.id);
        block.setUserId(this.userId);
        block.setUserName(this.userName);
        return block;
    }
}
