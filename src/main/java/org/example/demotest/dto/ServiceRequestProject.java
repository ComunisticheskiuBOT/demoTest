package org.example.demotest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.demotest.entities.enums.MaterialType;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRequestProject implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long projectId;
    private Long engineerId;
    private String projectName;
    private String drawing;
    private MaterialType materialType;
    private Duration expectedTime;
    private String projectDescription;
}
