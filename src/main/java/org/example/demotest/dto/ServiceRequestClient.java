package org.example.demotest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.demotest.entities.Reputation;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRequestClient implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long client_id; // Нарушаешь camelCase
    private String companyName;
    private String contactPerson;
    private String phoneNumber;
    private String email;
    private String address;
    private Reputation reputation;
}
