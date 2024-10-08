package org.example.demotest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.demotest.entities.Department;
import org.example.demotest.entities.Status;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRequestEmployee implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String firstName;
    private String secondName;
    private String lastName;
    private Date birthDate;
    private Long passportNumber;
    private String address;
    private String phoneNumber;
    private String email;
    private String password;
    private Date hireDate;
    private Date terminationDate;
    private Department department;
    private String position;
    private Status status;
    private Integer salary;
    private String comments;
}
