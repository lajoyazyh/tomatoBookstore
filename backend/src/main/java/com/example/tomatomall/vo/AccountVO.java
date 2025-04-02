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



    private Date createTime;

    public Account toPO(){
        Account account=new Account();
        account.setId(this.id);
        account.setUsername(this.username);
        account.setPassword(this.password);
        account.setName(this.name);
        account.setAvatar(this.avatar);
        account.setRole(this.role);
        account.setTelephone(this.telephone);
        account.setEmail(this.email);
        account.setLocation(this.location);
        return account;
    }
}
