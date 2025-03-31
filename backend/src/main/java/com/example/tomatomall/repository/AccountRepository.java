package com.example.tomatomall.repository;

import com.example.tomatomall.po.Account;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AccountRepository extends JpaRepository<Account, Integer> {
    Account findByTelephone(String telephone);
    Account findByTelephoneAndPassword(String telephone, String password);

    Account findByUsername(String username);
}
