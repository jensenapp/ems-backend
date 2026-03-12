package com.jensen.ems.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long employeeId;

    // HR 業務欄位

    @Column(name = "employee_code", unique = true)
    private String employeeCode; // 員工編號

    @Column(name = "department")
    private String department; // 部門

    @Column(name = "job_title")
    private String jobTitle; // 職稱

    @Column(name = "hire_date")
    private LocalDate hireDate; // 到職日

    @Column(name = "status")
    private String status = "Active"; // 預設為在職 (Active)

    //關聯欄位

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "account_id")
    private Account account;
}
