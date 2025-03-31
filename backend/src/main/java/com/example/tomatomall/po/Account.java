package com.example.tomatomall.po;

import com.example.tomatomall.enums.RoleEnum;
import com.example.tomatomall.vo.AccountVO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class Account {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;

    @Basic
    @Column(name = "name")
    private String name;

    @Basic
    @Column(name = "phone")
    private String phone;

    @Basic
    @Column(name = "password")
    private String password;

    //必须注意，在Java中用驼峰，在MySQL字段中用连字符_
    @Basic
    @Column(name = "create_time")
    private Date createTime;

    @Basic
    @Column(name = "store_id")
    private Integer storeId;

    @Basic
    @Column(name = "address")
    private String address;

    @Basic
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    public AccountVO toVO(){
        AccountVO accountVO=new AccountVO();
        accountVO.setId(this.id);
        accountVO.setAddress(this.address);
        accountVO.setName(this.name);
        accountVO.setRole(this.role);
        accountVO.setStoreId(this.storeId);
        accountVO.setPhone(this.phone);
        accountVO.setPassword(this.password);
        accountVO.setCreateTime(this.createTime);
        return accountVO;
    }
}
