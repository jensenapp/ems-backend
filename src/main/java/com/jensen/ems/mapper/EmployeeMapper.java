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

    public static Employee mapToEmployee(EmployeeDto employeeDto){
        Employee employee = new Employee();
        // 只有 Employee 專屬的欄位才在這裡 set
        employee.setEmployeeCode(employeeDto.getEmployeeCode());
        employee.setDepartment(employeeDto.getDepartment());
        employee.setJobTitle(employeeDto.getJobTitle());
        employee.setHireDate(employeeDto.getHireDate());
        employee.setStatus(employeeDto.getStatus());

        return employee;
    }
}
