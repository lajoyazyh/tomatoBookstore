package com.example.tomatomall.controller;

import com.example.tomatomall.po.Account;
import com.example.tomatomall.service.AccountService;
import com.example.tomatomall.vo.AccountVO;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.tomatomall.util.TokenUtil;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Resource
    AccountService accountService;

    @Autowired
    private TokenUtil tokenUtil;

    /**
     * 获取用户详情
     */
    @GetMapping("/{username}")
    public Response getUser(@RequestHeader("token") String token, @PathVariable("username") String username) {
        try{
            if(!tokenUtil.verifyToken(token)){
                return Response.buildFailure("401","未授权");
            }
            Account account=tokenUtil.getAccount(token);
            if(account==null||!account.getUsername().equals(username)){
                return Response.buildFailure("401","未授权");
            }

            AccountVO accountVO = accountService.getInformation(username);
            if(accountVO != null){
                return Response.buildSuccess(accountVO);
            }else{
                return Response.buildFailure("401", "用户不存在");
            }
        }catch (Exception e){
            return Response.buildFailure("401","未授权");
        }
    }

    /**
     * 创建新的用户
     */
    @PostMapping()
    public Response createUser(@RequestBody AccountVO accountVO) {
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
    @PutMapping()
    public Response updateInformation(@RequestHeader("token") String token, @RequestParam("accountVO") AccountVO accountVO) {
        try {
            if(!tokenUtil.verifyToken(token)){
                return Response.buildFailure("401","未授权");
            }
            Account account=tokenUtil.getAccount(token);
            if(account==null||!account.getUsername().equals(accountVO.getUsername())){
                return Response.buildFailure("401","未授权");
            }

            // 更新用户信息
            Boolean flag = accountService.updateInformation(accountVO);
            if (!flag) {
                return Response.buildFailure("400", "更新失败：缺少正确的用户名");
            } else {
                return Response.buildSuccess("更新成功");
            }
        } catch (Exception e) {
            return Response.buildFailure("401", "未授权");
        }
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Response login(@RequestParam("username") String username, @RequestParam("password") String password) {
        String token = accountService.login(username, password);
        if(token == "-1"){
            return Response.buildFailure("400", "用户不存在/用户密码错误");
        }else if(token != null){
            return Response.buildSuccess(token);
        }
        return Response.buildFailure("400", "你的后端方法实现错了，再回去沉淀沉淀！");
    }
}
