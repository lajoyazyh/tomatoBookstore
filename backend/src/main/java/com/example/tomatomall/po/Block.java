package com.example.tomatomall.po;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.example.tomatomall.vo.BlockVO;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@Entity
public class Block {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;

    @Basic
    @Column(name = "user_id")
    private Integer userId;//拉黑用户id

    @Basic
    @Column(name = "user_name")
    private String userName;//拉黑用户名

    public BlockVO toVO(){
        BlockVO blockVO=new BlockVO();
        blockVO.setId(this.id);
        blockVO.setUserId(this.userId);
        blockVO.setUserName(this.userName);
        return blockVO;
    }
}
