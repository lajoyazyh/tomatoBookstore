package com.example.tomatomall.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.tomatomall.repository.AccountRepository;
import com.example.tomatomall.po.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Author: DingXiaoyu && ZhuYehang
 * @Date: 0:28 2023/11/26 && 2025/3/31
 * 这是一个token的工具类，
 * 设置了过期时间为1天。
 * getToken方法用来获取token，
 * token中包含了用户的Id、密码信息以及到期时间。
 * verifyToken方法用来检验token是否正确。
 * getAccount方法用来从token中获得用户信息。
 */
@Component
public class TokenUtil {
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000;

    @Autowired
    AccountRepository accountRepository;

    public String getToken(Account account) {
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        return JWT.create()
                .withAudience(String.valueOf(account.getId()))
                .withExpiresAt(date)
                .sign(Algorithm.HMAC256(account.getPassword()));
    }

    public boolean verifyToken(String token) {
        try {
            Integer accountId=Integer.parseInt(JWT.decode(token).getAudience().get(0));
            Account account= accountRepository.findById(accountId).get();
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(account.getPassword())).build();
            jwtVerifier.verify(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public Account getAccount(String token){
        Integer accountId =Integer.parseInt(JWT.decode(token).getAudience().get(0));
        return accountRepository.findById(accountId).get();
    }
}
