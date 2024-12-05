package org.example.demotest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.demotest.entities.Employee;
import org.example.demotest.entities.Product;
import org.example.demotest.entities.enums.Result;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRequestQuality implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long qualityId;
    @JsonProperty("inspectorId")
    private Employee inspector;
    @JsonProperty("productId")
    private Product product;
    private Date inspectionDate;
    private Result result;
    private String comments;
}
