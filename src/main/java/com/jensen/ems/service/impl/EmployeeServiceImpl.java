package com.jensen.ems.service.impl;

import com.jensen.ems.dto.EmployeeDto;
import com.jensen.ems.dto.OnboardRequestDto;
import com.jensen.ems.entity.Account;
import com.jensen.ems.entity.Employee;
import com.jensen.ems.exception.ResourceNotFoundException;
import com.jensen.ems.mapper.EmployeeMapper;
import com.jensen.ems.repository.AccountRepository;
import com.jensen.ems.repository.EmployeeRepository;
import com.jensen.ems.repository.RoleRepository;
import com.jensen.ems.service.IEmployeeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements IEmployeeService {

    private final EmployeeRepository employeeRepository;

    private final AccountRepository accountRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;


    // 【新增的一鍵入職方法】
    @Transactional
    @Override
    public EmployeeDto onboardEmployee(OnboardRequestDto requestDto) {

        // 1. 檢查 Email 或手機是否已存在
        if (accountRepository.findByEmailOrMobileNumber(requestDto.getEmail(), requestDto.getMobileNumber()).isPresent()) {
            throw new RuntimeException("Email 或是手機號碼已經被註冊過了！");
        }

        // 2. 建立並儲存 Account
        Account account = new Account();
        account.setName(requestDto.getName());
        account.setEmail(requestDto.getEmail());
        account.setMobileNumber(requestDto.getMobileNumber());
        account.setPasswordHash(passwordEncoder.encode(requestDto.getPassword()));

        // 賦予預設的 USER 權限
      roleRepository.findByName("ROLE_USER").ifPresent(role -> account.setRoles(Set.of(role)));


        // 3. 建立並儲存 Employee
        Employee employee = new Employee();
        employee.setEmployeeCode(requestDto.getEmployeeCode());
        employee.setDepartment(requestDto.getDepartment());
        employee.setJobTitle(requestDto.getJobTitle());
        employee.setHireDate(requestDto.getHireDate());
        employee.setStatus("Active"); // 預設在職

        // 4. 綁定雙向關係
        account.setEmployee(employee);
        employee.setAccount(account);

        // 5. 儲存 (因為你在 Employee 實體上有加 cascade = CascadeType.PERSIST，存 Employee 時會連帶存 Account)
        Employee savedEmployee = employeeRepository.save(employee);

        return EmployeeMapper.mapToEmployeeDto(savedEmployee);
    }



    @Override
    public EmployeeDto getEmployeeById(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new ResourceNotFoundException("Employee is not exists with given id : " + employeeId));
        return EmployeeMapper.mapToEmployeeDto(employee);
    }

    @Override
    public List<EmployeeDto> getAllEmployess() {
        // 1. 取得當前登入者
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = authentication.getName();

        // 判斷是否為 ADMIN
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        List<Employee> employees;

        if (isAdmin) {
            // ADMIN: 撈出所有人
            employees = employeeRepository.findAll();
        } else {
            // USER: 透過 Email 去找綁定的那唯一一個 Account，再取得 Employee
            Account account = accountRepository.findByEmail(currentEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

            if(account.getEmployee() != null) {
                // 如果有綁定，就只回傳包含自己那一筆的 List
                employees = List.of(account.getEmployee());
            } else {
                // 如果還沒綁定，回傳空陣列
                employees = List.of();
            }
        }

        return employees.stream().map(EmployeeMapper::mapToEmployeeDto).toList();
    }

    @Override
    @Transactional // 加上 Transactional，確保兩張表同時更新成功
    public EmployeeDto updateEmployee(Long id, EmployeeDto employeeDto) {
        // 1. 找出員工
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee is not exists with given id: " + id));

        // 2. 找出關聯的帳號
        Account account = employee.getAccount();
        if (account == null) {
            throw new ResourceNotFoundException("Account not found for this employee");
        }

        // 3. 更新 Account 表的資料 (姓名、信箱、手機)
        account.setName(employeeDto.getName());
        account.setEmail(employeeDto.getEmail());
        account.setMobileNumber(employeeDto.getMobileNumber());

        // 4. 更新 Employee 表的專屬資料 (HR 資訊)
        employee.setEmployeeCode(employeeDto.getEmployeeCode());
        employee.setDepartment(employeeDto.getDepartment());
        employee.setJobTitle(employeeDto.getJobTitle());
        employee.setHireDate(employeeDto.getHireDate());
        employee.setStatus(employeeDto.getStatus());

        // 5. 儲存 (因為 @Transactional，通常只要 save employee 就好，JPA 會自動 cascade)
        accountRepository.save(account); // 為了保險起見，明確 save account
        Employee updatedEmployee = employeeRepository.save(employee);

        return EmployeeMapper.mapToEmployeeDto(updatedEmployee);
    }

    @Override
    public void deleteEmployee(Long id) {
      employeeRepository.deleteById(id);
    }

}
