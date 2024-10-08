package org.example.demotest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.demotest.entities.Status;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRequestDepartment implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long departmentId;
    private String departmentName;
    private String location;
    private String description;
}
