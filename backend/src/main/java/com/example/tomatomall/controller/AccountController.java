package com.example.tomatomall.controller;

import com.example.tomatomall.service.AccountService;
import com.example.tomatomall.vo.Response;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Resource
    AccountService accountService;

    /**
     * 获取用户详情
     */
    @GetMapping("/info")
    public Response getUser() {
        return Response.buildSuccess(accountService.getInformation());
    }

    /**
     * 创建新的用户
     */
    @PostMapping("/register")
    public Response createUser() {
        return null;
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Response updateUser() {
        return null;
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Response login() {
        return null;
    }
}
