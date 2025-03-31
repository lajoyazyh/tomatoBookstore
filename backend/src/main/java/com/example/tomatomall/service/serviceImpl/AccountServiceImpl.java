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

import java.util.Date;

/**
 * @Author: ZhuYehang
 * @Date: 2025/3/31
 *
 * 注册登录功能实现
 */
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepository accountRepositoryRepository;

    @Autowired
    TokenUtil tokenUtil;

    @Autowired
    SecurityUtil securityUtil;


    @Override
    public Boolean register(AccountVO accountVO) {
        Account account = accountRepositoryRepository.findByTelephone(accountVO.getTelephone());
        if (account != null) {
            throw TomatoMallException.phoneAlreadyExists();
        }
        Account newAccount = accountVO.toPO();
        accountRepositoryRepository.save(newAccount);
        return true;
    }

    @Override
    public String login(String phone, String password) {
        Account account = accountRepositoryRepository.findByTelephoneAndPassword(phone, password);
        if (account == null) {
            throw TomatoMallException.phoneOrPasswordError();
        }
        return tokenUtil.getToken(account);
    }

    @Override
    public AccountVO getInformation() {
        Account account=securityUtil.getCurrentAccount();
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
        accountRepositoryRepository.save(account);
        return true;
    }

}
