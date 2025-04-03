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
@Entity
public class Account {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;

    @Basic
    @Column(name = "username")
    private String username;

    @Basic
    @Column(name = "password")
    private String password;

    @Basic
    @Column(name = "name")
    private String name;

    @Basic
    @Column(name = "avatar")
    private String avatar;

    @Basic
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    @Basic
    @Column(name = "telephone")
    private String telephone;

    @Basic
    @Column(name = "email")
    private String email;

    @Basic
    @Column(name = "location")
    private String location;


    public AccountVO toVO(){
        AccountVO accountVO=new AccountVO();
        accountVO.setId(this.id);
        accountVO.setUsername(this.username);
        accountVO.setPassword(this.password);
        accountVO.setName(this.name);
        accountVO.setAvatar(this.avatar);
        accountVO.setRole(this.role);
        accountVO.setEmail(this.email);
        accountVO.setTelephone(this.telephone);
        accountVO.setLocation(this.location);
        return accountVO;
    }
}
