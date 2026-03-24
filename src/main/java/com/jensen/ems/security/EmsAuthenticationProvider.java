package com.jensen.ems.security;


import com.jensen.ems.entity.Account;
import com.jensen.ems.entity.Role;
import com.jensen.ems.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EmsAuthenticationProvider implements AuthenticationProvider {
    
    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String email = authentication.getName();
        String pwd = authentication.getCredentials().toString();

        Account account = accountRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User details not found for the user:"+email));

        Set<Role> roles = account.getRoles();

        List<GrantedAuthority> authorities=roles.
                stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());

        if (passwordEncoder.matches(pwd, account.getPasswordHash())){
            return new UsernamePasswordAuthenticationToken(account,null, authorities);
        }else {
            throw new BadCredentialsException("Invalid password!");
        }

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
