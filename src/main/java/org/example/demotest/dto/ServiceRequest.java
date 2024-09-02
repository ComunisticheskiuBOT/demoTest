package org.example.demotest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.demotest.entities.Role;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String first_name;
    private String second_name;
    private String last_name;
    private Role role;
    private String user_description;
    private String user_password;
}
