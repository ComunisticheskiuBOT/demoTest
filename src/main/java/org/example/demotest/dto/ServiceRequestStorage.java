package org.example.demotest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.demotest.entities.Product;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRequestStorage implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long storageId;

    @JsonProperty("productId")
    private Product product;

    private Integer quantity;
    private Date arrivalDate;
}
