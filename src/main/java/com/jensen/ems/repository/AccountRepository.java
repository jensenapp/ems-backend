package com.jensen.ems.repository;


import com.jensen.ems.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account,Long> {

    Optional<Account> findByEmail(String email);

    Optional<Account> findByEmailOrMobileNumber(String email, String mobileNumber);
}
