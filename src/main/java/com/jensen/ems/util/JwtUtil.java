package com.jensen.ems.util;


import com.jensen.ems.constants.ApplicationConstants;
import com.jensen.ems.entity.Account;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final Environment env;

    public String generateJwtToken(Authentication authentication) {
        String jwt = "";

        // 1. 讀取密鑰（優先使用環境變數）
        String secret = env.getProperty(
                ApplicationConstants.JWT_SECRET_KEY,
                ApplicationConstants.JWT_SECRET_DEFAULT_VALUE
        );

        // 2. 將密鑰轉換為 SecretKey 物件
        SecretKey secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );

        // 3. 從 Authentication 中提取使用者資訊
        Account fetchedUser = (Account) authentication.getPrincipal();

        // 4. 建立 JWT Token
        jwt = Jwts.builder()
                .issuer("EMS Application")                          // 發行者
                .subject("JWT Token")                          // 主旨
                .claim("username", fetchedUser.getName())  // 自訂聲明
                .claim("email",fetchedUser.getEmail())
                .claim("mobileNumber",fetchedUser.getMobileNumber())
                .claim("roles", authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(",")))
                .issuedAt(new Date())                          // 發行時間
                .expiration(new Date(new Date().getTime() + 60 * 60*1000))  // 過期時間：60分鐘
                .signWith(secretKey)                           // 使用密鑰簽署
                .compact();                                    // 生成最終 Token

        return jwt;
    }
}
