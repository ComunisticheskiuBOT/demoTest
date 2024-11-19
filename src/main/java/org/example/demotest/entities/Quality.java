package org.example.demotest.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@SuperBuilder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "Quality")
public class Quality {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quality_id")
    private Long qualityId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "inspector_id", referencedColumnName = "id")
    // я бы лучше назван inspector_employee_id
    private Employee inspector;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private Product product;

    @Column(name = "inspection_date")
    private Date inspectionDate; // Instant / LocalDate
    @Column(name = "result")
    private Result result;
    @Column(name = "comments")
    // 255 Символов не маловато?
    private String comments;

    @JsonProperty("inspectorId")
    public Long getInspectorId() {
        return inspector != null ? inspector.getId() : null;
    }
    @JsonProperty("productId")
    public Long getProductId() {
        return product != null ? product.getProductId() : null;
    }
}