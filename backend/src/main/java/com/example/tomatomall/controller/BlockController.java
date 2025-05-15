package com.example.tomatomall.controller;

import com.example.tomatomall.enums.RoleEnum;
import com.example.tomatomall.po.Account;

import com.example.tomatomall.repository.AccountRepository;
import com.example.tomatomall.repository.BlockRepository;
import com.example.tomatomall.service.BlockService;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.tomatomall.util.TokenUtil;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/blocks")
public class BlockController {
    @Resource
    BlockService blockService;

    @Autowired
    BlockRepository blockRepository;

    @Autowired
    AccountRepository accoutRepository;

    @Autowired
    private TokenUtil tokenUtil;

    /**
     * 拉黑用户
     */
    @PostMapping
    public Response addBlock(@RequestHeader("token") String token,@RequestParam(value = "userId")Integer userId){
        if(!tokenUtil.verifyToken(token)){
            return Response.buildFailure("400","未登录");
        }
        Account thisAccount=tokenUtil.getAccount(token);
        if(thisAccount.getRole()== RoleEnum.CUSTOMER){
            return Response.buildFailure("401","您没有该权限");
        }else{
            Optional<Account> account=accoutRepository.findById(userId);
            if(!account.isPresent()){
                return Response.buildFailure("400","该用户不存在");
            }
            if(account.get().getRole()==RoleEnum.CUSTOMER){
                blockService.addBlock(userId);
                return Response.buildSuccess("该用户已被拉黑");
            }else{
                return Response.buildFailure("400","不能拉黑管理员");
            }

        }
    }

    /**
     * 获取所有拉黑的用户
     */
    @GetMapping
    public Response getAllBlock(@RequestHeader("token") String token){
        if(!tokenUtil.verifyToken(token)){
            return Response.buildFailure("400","未登录");
        }
        Account thisAccount=tokenUtil.getAccount(token);
        if(thisAccount.getRole()== RoleEnum.CUSTOMER){
            return Response.buildFailure("401","您没有该权限");
        }else{
            return Response.buildSuccess(blockService.getAllBlock());
        }
    }

    /**
     * 判断某一用户是否被拉黑
     */
    @GetMapping("/{userId}")
    public Response judgeBlock(@PathVariable(value = "userId")Integer userId){
        Optional<Account> thisAccount=accoutRepository.findById(userId);
        if(!thisAccount.isPresent()){
            return Response.buildFailure("400","该用户不存在");
        }
        Boolean res=blockService.judgeBlock(userId);
        return Response.buildSuccess(res);
    }

    /**
     * 取消拉黑
     */
    @DeleteMapping
    public Response deleteBlock(@RequestHeader("token") String token,@RequestParam(value = "id")Integer id){
        if(!tokenUtil.verifyToken(token)){
            return Response.buildFailure("400","未登录");
        }
        Account thisAccount=tokenUtil.getAccount(token);
        if(thisAccount.getRole()== RoleEnum.CUSTOMER){
            return Response.buildFailure("401","您没有该权限");
        }else{
            blockService.deleteBlock(id);
            return Response.buildSuccess("取消成功");
        }
    }
}
