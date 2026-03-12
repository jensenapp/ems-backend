package com.jensen.ems.controller;


import com.jensen.ems.dto.*;
import com.jensen.ems.entity.Account;
import com.jensen.ems.repository.AccountRepository;
import com.jensen.ems.repository.RoleRepository;
import com.jensen.ems.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;

    private final AuthenticationManager authenticationManager;



    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> apiLogin(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            // 1. 執行驗證邏輯
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.username(),
                            loginRequestDto.password()
                    )
            );

            var loggedInAccount = (Account) authentication.getPrincipal(); // 取得登入者詳細資訊

            UserDto userDto = new UserDto();


            BeanUtils.copyProperties(loggedInAccount,userDto);

            if (loggedInAccount.getEmployee() != null) {
                userDto.setEmployeeId(loggedInAccount.getEmployee().getEmployeeId());
            }

            userDto.setUserId(loggedInAccount.getAccountId());

            // 從 authentication 物件中取出 authorities 並轉為逗號分隔字串 (例如: "ROLE_USER,ROLE_ADMIN")
           userDto.setRoles(authentication.getAuthorities()
                   .stream()
                   .map(GrantedAuthority::getAuthority)
                   .collect(Collectors.joining(",")));


            String jwtToken = jwtUtil.generateJwtToken(authentication);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new LoginResponseDto(HttpStatus.OK.getReasonPhrase(),
                            userDto, jwtToken));


        } catch (BadCredentialsException ex) {
            // 3. 捕捉帳密錯誤 -> 回傳 401
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid username or password");

        } catch (AuthenticationException ex) {
            // 4. 捕捉其他驗證錯誤 -> 回傳 401
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Authentication failed");

        } catch (Exception ex) {
            // 5. 捕捉未知錯誤 -> 回傳 500
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        }
    }

    private ResponseEntity<LoginResponseDto> buildErrorResponse(
            HttpStatus status, String message) {

        return ResponseEntity
                .status(status)
                .body(new LoginResponseDto(message, null, null));
    }


}