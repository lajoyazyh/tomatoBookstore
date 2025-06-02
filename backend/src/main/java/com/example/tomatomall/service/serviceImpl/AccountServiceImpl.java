package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.enums.RoleEnum;
import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Account;
import com.example.tomatomall.repository.AccountRepository;
import com.example.tomatomall.service.AccountService;
import com.example.tomatomall.util.SecurityUtil;
import com.example.tomatomall.util.TokenUtil;
import com.example.tomatomall.vo.AccountVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public String register(AccountVO accountVO) {
        Account account = accountRepository.findByUsername(accountVO.getUsername());
        if (account != null) {
            // throw TomatoMallException.usernameAlreadyExists();
            return "用户名已存在";
        }
        //对密码进行加密然后重新存储
        String newPassword = accountVO.getPassword();
        String encodePassword =passwordEncoder.encode(newPassword);
        accountVO.setPassword(encodePassword);
        Account newAccount = accountVO.toPO();

        accountRepository.save(newAccount);
        return "注册成功";
    }

    @Override
    public String login(String username, String password) {
        Account account = accountRepository.findByUsername(username);
        String thispassword = account.getPassword();
        if (!passwordEncoder.matches(password, thispassword)) {
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
        String username = accountVO.getUsername();
        if(username == null){
            return false;
        }
        Account account=accountRepository.findByUsername(username);
        if(account == null){
            return false;
        }
        if (accountVO.getPassword()!=null){
            String encodePassword =passwordEncoder.encode(accountVO.getPassword());
            account.setPassword(encodePassword);
        }
        if (accountVO.getName()!=null){
            account.setName(accountVO.getName());
        }
        if (accountVO.getAvatar()!=null){
            account.setAvatar(accountVO.getAvatar());
        }
        if (accountVO.getRole()!=null){
            account.setRole(accountVO.getRole());
        }
        if (accountVO.getTelephone()!=null){
            account.setTelephone(accountVO.getTelephone());
        }
        if (accountVO.getEmail()!=null){
            account.setEmail(accountVO.getEmail());
        }
        if (accountVO.getLocation()!=null){
            account.setLocation(accountVO.getLocation());
        }
        accountRepository.save(account);
        return true;
    }

    @Override
    public List<AccountVO> getAllUser(){
        List<Account> allUser=accountRepository.findAll();
        List<AccountVO> allUserVO=new ArrayList<>();
        for(Account account:allUser){
            if(account.getRole()== RoleEnum.CUSTOMER){
                allUserVO.add(account.toVO());
            }
        }
        return allUserVO;
    }

    @Override
    public List<AccountVO> getUsers(String partialUsername){
        List<Account> allUsers=accountRepository.findAll();
        List<AccountVO> thisAccountsVO=new ArrayList<>();
        for(Account account:allUsers){
            if(account.getUsername().contains(partialUsername)&&account.getRole()==RoleEnum.CUSTOMER){
                thisAccountsVO.add(account.toVO());
            }
        }
        return thisAccountsVO;
    }
}
