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

    private String username;

    private String password;

    private String name;

    private String avatar;

    private RoleEnum role;

    private String telephone;

    private String email;

    private String location;

//    private Integer storeId;
//
//    private String address;



    private Date createTime;

    public Account toPO(){
        Account Account=new Account();
        Account.setId(this.id);
        Account.setUsername(this.username);
        Account.setPassword(this.password);
        Account.setName(this.name);
        Account.setAvatar(this.avatar);
        Account.setRole(this.role);
        Account.setTelephone(this.telephone);
        Account.setEmail(this.email);
        Account.setLocation(this.location);
        return Account;
    }
}
