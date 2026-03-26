package com.jensen.ems.controller;


import com.jensen.ems.dto.EmployeeDto;
import com.jensen.ems.dto.OnboardRequestDto;
import com.jensen.ems.dto.ResponseDto;
import com.jensen.ems.entity.Account;
import com.jensen.ems.entity.Employee;
import com.jensen.ems.exception.ResourceNotFoundException;
import com.jensen.ems.repository.AccountRepository;
import com.jensen.ems.repository.EmployeeRepository;
import com.jensen.ems.service.IEmployeeService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employees")
public class EmployeeController {
    private final IEmployeeService employeeService;
    private final EmployeeRepository employeeRepository;
    private final AccountRepository accountRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeDto> onboardEmployee(@RequestBody @Valid OnboardRequestDto requestDto){
        // Admin 呼叫這支 API，一次完成所有動作
        EmployeeDto employee = employeeService.onboardEmployee(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(employee);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @accountSecurityService.isOwner(authentication,#id)")
   public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable Long id){
        EmployeeDto employeeById = employeeService.getEmployeeById(id);
        return ResponseEntity.status(HttpStatus.OK).body(employeeById);
    }

    @GetMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EmployeeDto>> getAllEmployees(){
       List<EmployeeDto> allEmployess = employeeService.getAllEmployess();
       return ResponseEntity.status(HttpStatus.OK).body(allEmployess);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @accountSecurityService.isOwner(authentication,#id)")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable Long id,@RequestBody EmployeeDto employeeDto){
        EmployeeDto employee = employeeService.updateEmployee(id, employeeDto);
        return ResponseEntity.status(HttpStatus.OK).body(employee);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<ResponseDto> deleteEmployee(@PathVariable Long id){

        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee is not exists with given id: " + id));

        Account account = employee.getAccount();

        if (account != null) {
            employee.setAccount(null);
            account.setEmployee(null);
        }

        employeeRepository.delete(employee);

        if (account !=null){
            accountRepository.delete(account);
        }


        return ResponseEntity.ok(new ResponseDto("200","Employee deleted successfully!."));
    }
}
