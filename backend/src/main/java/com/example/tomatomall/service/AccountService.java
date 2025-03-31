package com.example.tomatomall.service;

import com.example.tomatomall.vo.AccountVO;

public interface AccountService {
    String register(AccountVO accountVO);

    String login(String phone,String password);

    AccountVO getInformation(String username);

    Boolean updateInformation(AccountVO accountVO);

}
