package com.example.tomatomall.repository;

import com.example.tomatomall.po.Account;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AccountRepository extends JpaRepository<Account, Integer> {

    Account findByUsernameAndPassword(String username, String password);

    Account findByUsername(String username);
}
