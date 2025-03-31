package com.example.tomatomall.vo;

import com.example.tomatomall.po.Account;
import com.example.tomatomall.enums.RoleEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor

public class AccountVO {
    private Integer id;

    private String name;

    private String phone;

    private String password;

    private Integer storeId;

    private String address;

    private RoleEnum role;

    private Date createTime;

    public Account toPO(){
        Account Account=new Account();
        Account.setId(this.id);
        Account.setAddress(this.address);
        Account.setName(this.name);
        Account.setPhone(this.phone);
        Account.setRole(this.role);
        Account.setStoreId(this.storeId);
        Account.setPassword(this.password);
        Account.setCreateTime(this.createTime);
        return Account;
    }
}
