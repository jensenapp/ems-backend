package com.jensen.ems.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {
    private Long employeeId;

    // 來自 Account 表的資料
    private String name;
    private String email;
    private String mobileNumber;

    // 來自 Employee 表的資料
    private String employeeCode;
    private String department;
    private String jobTitle;
    private LocalDate hireDate;
    private String status;
}
