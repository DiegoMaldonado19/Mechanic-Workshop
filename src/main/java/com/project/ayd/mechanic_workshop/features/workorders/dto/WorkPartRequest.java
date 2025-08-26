package com.project.ayd.mechanic_workshop.features.workorders.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkPartRequest {

    @NotNull(message = "Work ID is required")
    @Positive(message = "Work ID must be positive")
    private Long workId;

    @NotNull(message = "Part ID is required")
    @Positive(message = "Part ID must be positive")
    private Long partId;

    @NotNull(message = "Quantity needed is required")
    @Positive(message = "Quantity needed must be positive")
    @Max(value = 9999, message = "Quantity needed cannot exceed 9999")
    private Integer quantityNeeded;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.01", message = "Unit price must be greater than 0")
    @DecimalMax(value = "99999999.99", message = "Unit price cannot exceed 99999999.99")
    @Digits(integer = 8, fraction = 2, message = "Unit price must have at most 8 integer digits and 2 decimal places")
    private BigDecimal unitPrice;
}