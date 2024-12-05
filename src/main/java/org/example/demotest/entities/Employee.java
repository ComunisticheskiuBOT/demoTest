package org.example.demotest.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.example.demotest.entities.enums.Role;
import org.example.demotest.entities.enums.Status;

import java.util.Date;
@Data
@SuperBuilder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "\"employees\"")
public class Employee {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "second_name")
    private String secondName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "birth_date")
    private Date birthDate;
    @Column(name = "passport_number")
    private Long passportNumber;
    @Column(name = "inn")
    private Long inn;
    @Column(name = "snils")
    private Long snils;
    @Column(name = "address")
    private String address;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String passWord;
    @Column(name = "hire_date")
    private Date hireDate;
    @Column(name = "termination_date")
    private Date terminationDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", referencedColumnName = "department_id")
    private Department department;

    @Column(name = "position")
    private String position;
    @Column(name = "role")
    private Role role;
    @Column(name = "status")
    private Status status;
    @Column(name = "salary")
    private Integer salary;
    @Column(name = "comments")
    private String comments;
}
