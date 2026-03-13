package com.jensen.ems.service;

import com.jensen.ems.dto.EmployeeDto;
import com.jensen.ems.dto.OnboardRequestDto;

import java.util.List;

public interface IEmployeeService {

    EmployeeDto onboardEmployee(OnboardRequestDto requestDto);
    EmployeeDto getEmployeeById(Long id);


    List<EmployeeDto> getAllEmployess();

    EmployeeDto updateEmployee(Long id,EmployeeDto employeeDto);
    void deleteEmployee(Long id);

}
