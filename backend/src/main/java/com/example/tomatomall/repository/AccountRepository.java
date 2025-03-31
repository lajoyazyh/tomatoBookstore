package com.example.tomatomall.repository;

import com.example.tomatomall.po.Account;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AccountRepository extends JpaRepository<Account, Integer> {
    Account findByPhone(String phone);
    Account findByPhoneAndPassword(String phone, String password);
}
