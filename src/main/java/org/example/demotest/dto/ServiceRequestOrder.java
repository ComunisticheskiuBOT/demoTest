package org.example.demotest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.demotest.entities.OrderStatus;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRequestOrder implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long orderId;
    private Long clientId;
    private Long projectId;
    private Date dateOfOrder;
    private Date dateOfExecution;
    private OrderStatus orderStatus;
    private String orderDescription;
}