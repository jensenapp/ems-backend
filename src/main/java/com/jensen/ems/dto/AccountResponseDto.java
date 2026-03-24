package com.jensen.ems.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class AccountResponseDto {

    private Long accountId;
    private String name;
    private String email;
    private String mobileNumber;
    private String roles;

    private Long employeeId;
}
