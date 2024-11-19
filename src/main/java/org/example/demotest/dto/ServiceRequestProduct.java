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
    private Project project; // Вроде из JSON тянешь как айдишник, а сериализуешь в объект. Может вводить в заблуждение
    /*
    Тем более что в других местах у тебя есть <object>Id - и это айдишник
     */
    private String productName;
    private ProductType productType;
    private Integer quantity; // Заменить на BigInteger
    private Double weight; // Заменить на BigDecimal
    private Integer cost; // Заменить на BigDecimal

    /*

    В деньгах, количестве, массе и тд лучше использовать около бесконечные типы
    BigInteger - для целых
    BigDecimal - для дробных

     */
}
