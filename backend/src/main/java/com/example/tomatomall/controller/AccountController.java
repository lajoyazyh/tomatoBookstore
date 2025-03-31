package com.example.tomatomall.controller;

import com.example.tomatomall.po.Account;
import com.example.tomatomall.service.AccountService;
import com.example.tomatomall.vo.AccountVO;
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
    @GetMapping("/{username}")
    public Response getUser(@RequestParam("username") String username) {
        AccountVO accountVO = accountService.getInformation(username);
        if(accountVO != null){
            return Response.buildSuccess(accountVO);
        }else{
            return Response.buildFailure("401", null);
        }

    }

    /**
     * 创建新的用户
     */
    @PostMapping()
    public Response createUser(AccountVO accountVO) {
        String res = accountService.register(accountVO);
        if(res == "用户名已存在") {
            return Response.buildFailure("400", res);
        }else if(res == "注册成功"){
            return Response.buildSuccess(res);
        }
        return Response.buildFailure("400", "你的后端方法实现错了，再回去沉淀沉淀！");
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
    public Response login(@RequestParam("username") String username, @RequestParam("password") String password) {
        return Response.buildSuccess(accountService.login(username, password));
    }
}
