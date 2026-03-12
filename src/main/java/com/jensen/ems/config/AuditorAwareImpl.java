package com.jensen.ems.config;

import com.jensen.ems.entity.Account;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
@Component("auditorAwareImpl")
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // 1. 取得 Authentication 物件
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. 判斷是否為「未登入」或「匿名使用者」
        // 注意：Spring Security 會給予未登入訪問路徑的使用者 "anonymousUser" 的身分
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.of("Anonymous user");
        }

        // 3. 已登入，取得 Principal (登入者主體)
        Object principal = authentication.getPrincipal();
        String username;

        // 4. 判斷 Principal 型別並提取 Username (Email)
        if (principal instanceof Account account) {
            // 如果 Principal 是自定義的 Account 物件
            username = account.getEmail();
        } else {
            // 如果 Principal 是 String 或其他型別 (Fallback)
            username = principal.toString();
        }

        return Optional.of(username);
    }
}