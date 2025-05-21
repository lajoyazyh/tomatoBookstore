package com.example.tomatomall.service;

import com.example.tomatomall.vo.AccountVO;

import java.util.List;

public interface AccountService {
    String register(AccountVO accountVO);

    String login(String username,String password);

    AccountVO getInformation(String username);

    Boolean updateInformation(AccountVO accountVO);

    List<AccountVO> getAllUser();
}
