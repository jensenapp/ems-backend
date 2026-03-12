
package com.jensen.ems.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class OnboardRequestDto {
    // --- 帳號所需資訊 ---
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "The length of the name should be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email address must be a valid value")
    private String email;

    @NotBlank(message = "Mobile Number is required")
    @Pattern(regexp = "^\\d{10}$", message = "Mobile number must be exactly 10 digits")
    private String mobileNumber;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 20, message = "Password length must be between 8 and 20 characters")
    private String password;

    // --- 員工所需資訊 ---
    @NotBlank(message = "Employee Code is required")
    private String employeeCode;

    @NotBlank(message = "Department is required")
    private String department;

    @NotBlank(message = "Job Title is required")
    private String jobTitle;


    private LocalDate hireDate;
}