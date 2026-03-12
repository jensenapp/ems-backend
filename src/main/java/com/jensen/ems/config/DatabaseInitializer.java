// file: src/main/java/com/jensen/ems/config/DatabaseInitializer.java
package com.jensen.ems.config;

import com.jensen.ems.entity.Account;
import com.jensen.ems.entity.Role;
import com.jensen.ems.repository.AccountRepository;
import com.jensen.ems.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        // 1. 檢查並初始化「角色 (Roles)」
        Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> {
            Role role = new Role();
            role.setName("ROLE_ADMIN");
            return roleRepository.save(role);
        });

        Role userRole = roleRepository.findByName("ROLE_USER").orElseGet(() -> {
            Role role = new Role();
            role.setName("ROLE_USER");
            return roleRepository.save(role);
        });

        // 2. 檢查並建立「預設管理員帳號」
        String defaultAdminEmail = "admin@admin.com";
        if (accountRepository.findByEmail(defaultAdminEmail).isEmpty()) {
            Account adminAccount = new Account();
            adminAccount.setName("System Admin");
            adminAccount.setEmail(defaultAdminEmail);
            adminAccount.setMobileNumber("0999888777");

            // 預設密碼設為 Admin@123
            adminAccount.setPasswordHash(passwordEncoder.encode("Admin@123"));

            // 賦予管理員角色
            adminAccount.setRoles(Set.of(adminRole));

            accountRepository.save(adminAccount);

            System.out.println("=================================================");
            System.out.println("預設管理員帳號已建立！");
            System.out.println("帳號: " + defaultAdminEmail);
            System.out.println("密碼: Admin@123");
            System.out.println("=================================================");
        }
    }
}