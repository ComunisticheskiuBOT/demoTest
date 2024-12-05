package org.example.demotest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRequestMeasurement implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long measurement_id;
    private String name;
    private String description;
}
