package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Account;
import com.example.tomatomall.repository.AccountRepository;
import com.example.tomatomall.service.AccountService;
import com.example.tomatomall.util.SecurityUtil;
import com.example.tomatomall.util.TokenUtil;
import com.example.tomatomall.vo.AccountVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: ZhuYehang
 * @Date: 2025/3/31
 *
 * 注册登录功能实现
 */
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TokenUtil tokenUtil;

    @Autowired
    SecurityUtil securityUtil;


    @Override
    public String register(AccountVO accountVO) {
        Account account = accountRepository.findByUsername(accountVO.getUsername());
        if (account != null) {
            // throw TomatoMallException.usernameAlreadyExists();
            return "用户名已存在";
        }
        Account newAccount = accountVO.toPO();
        accountRepository.save(newAccount);
        return "注册成功";
    }

    @Override
    public String login(String username, String password) {
        Account account = accountRepository.findByUsernameAndPassword(username, password);
        if (account == null) {
            // throw TomatoMallException.accountOrPasswordError();
            return "-1";
        }
        return tokenUtil.getToken(account);
    }

    @Override
    public AccountVO getInformation(String username) {
        Account account=accountRepository.findByUsername(username);
        return account.toVO();
    }

    @Override
    public Boolean updateInformation(AccountVO accountVO) {
        Account account=securityUtil.getCurrentAccount();
        if (accountVO.getPassword()!=null){
            account.setPassword(accountVO.getPassword());
        }
        if (accountVO.getUsername()!=null){
            account.setUsername(accountVO.getUsername());
        }
        if (accountVO.getLocation()!=null){
            account.setLocation(accountVO.getLocation());
        }
        accountRepository.save(account);
        return true;
    }

}
