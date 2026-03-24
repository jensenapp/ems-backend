package com.jensen.ems.mapper;


import com.jensen.ems.dto.EmployeeDto;
import com.jensen.ems.entity.Employee;

public class EmployeeMapper {

    public static EmployeeDto mapToEmployeeDto(Employee employee){
        return new EmployeeDto(
                employee.getEmployeeId(),
                // 讀取關聯的 Account 資訊
                employee.getAccount().getName(),
                employee.getAccount().getEmail(),
                employee.getAccount().getMobileNumber(),
                // 讀取 Employee 自身資訊
                employee.getEmployeeCode(),
                employee.getDepartment(),
                employee.getJobTitle(),
                employee.getHireDate(),
                employee.getStatus()
        );
    }
}
