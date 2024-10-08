package org.example.demotest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.demotest.entities.MaterialType;
import org.example.demotest.entities.ProductType;
import org.example.demotest.entities.Project;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRequestProduct implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long productId;
    @JsonProperty("projectId")
    private Project project;
    private String productName;
    private ProductType productType;
    private Integer quantity;
    private Double weight;
    private Integer cost;
}
