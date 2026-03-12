package com.jensen.ems.security;

import com.jensen.ems.entity.Employee;
import com.jensen.ems.exception.ResourceNotFoundException;
import com.jensen.ems.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service("accountSecurityService")
@RequiredArgsConstructor
public class AccountSecurityService {

    private final EmployeeRepository employeeRepository;

    public boolean isOwner(Authentication authentication,Long employeeId){

        String currenyLoginEmail = authentication.getName();

        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new ResourceNotFoundException("user not found"));

        if (employee == null || employee.getAccount() == null) {
            return false;
        }

        return currenyLoginEmail.equals(employee.getAccount().getEmail());
    }

}
