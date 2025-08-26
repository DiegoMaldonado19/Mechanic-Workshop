package com.project.ayd.mechanic_workshop.features.workorders.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotationRequest {

    @NotNull(message = "Work ID is required")
    @Positive(message = "Work ID must be positive")
    private Long workId;

    @NotNull(message = "Total parts cost is required")
    @DecimalMin(value = "0.00", message = "Total parts cost cannot be negative")
    @DecimalMax(value = "99999999.99", message = "Total parts cost cannot exceed 99999999.99")
    @Digits(integer = 8, fraction = 2, message = "Total parts cost must have at most 8 integer digits and 2 decimal places")
    private BigDecimal totalPartsCost;

    @NotNull(message = "Total labor cost is required")
    @DecimalMin(value = "0.00", message = "Total labor cost cannot be negative")
    @DecimalMax(value = "99999999.99", message = "Total labor cost cannot exceed 99999999.99")
    @Digits(integer = 8, fraction = 2, message = "Total labor cost must have at most 8 integer digits and 2 decimal places")
    private BigDecimal totalLaborCost;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.01", message = "Total amount must be greater than 0")
    @DecimalMax(value = "99999999.99", message = "Total amount cannot exceed 99999999.99")
    @Digits(integer = 8, fraction = 2, message = "Total amount must have at most 8 integer digits and 2 decimal places")
    private BigDecimal totalAmount;

    @Future(message = "Valid until date must be in the future")
    private LocalDate validUntil;
}