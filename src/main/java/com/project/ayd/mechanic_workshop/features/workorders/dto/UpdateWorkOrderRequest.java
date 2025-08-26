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
public class UpdateWorkOrderRequest {

    @Size(min = 10, max = 1000, message = "Problem description must be between 10 and 1000 characters")
    private String problemDescription;

    @DecimalMin(value = "0.1", message = "Estimated hours must be greater than 0")
    @DecimalMax(value = "999.99", message = "Estimated hours cannot exceed 999.99")
    @Digits(integer = 3, fraction = 2, message = "Estimated hours must have at most 3 integer digits and 2 decimal places")
    private BigDecimal estimatedHours;

    @DecimalMin(value = "0.1", message = "Actual hours must be greater than 0")
    @DecimalMax(value = "999.99", message = "Actual hours cannot exceed 999.99")
    @Digits(integer = 3, fraction = 2, message = "Actual hours must have at most 3 integer digits and 2 decimal places")
    private BigDecimal actualHours;

    @DecimalMin(value = "0.01", message = "Estimated cost must be greater than 0")
    @DecimalMax(value = "99999999.99", message = "Estimated cost cannot exceed 99999999.99")
    @Digits(integer = 8, fraction = 2, message = "Estimated cost must have at most 8 integer digits and 2 decimal places")
    private BigDecimal estimatedCost;

    @DecimalMin(value = "0.01", message = "Actual cost must be greater than 0")
    @DecimalMax(value = "99999999.99", message = "Actual cost cannot exceed 99999999.99")
    @Digits(integer = 8, fraction = 2, message = "Actual cost must have at most 8 integer digits and 2 decimal places")
    private BigDecimal actualCost;

    @Min(value = 1, message = "Priority level must be at least 1")
    @Max(value = 5, message = "Priority level cannot exceed 5")
    private Integer priorityLevel;

    @Positive(message = "Work status ID must be positive")
    private Long workStatusId;

    @Positive(message = "Assigned employee ID must be positive")
    private Long assignedEmployeeId;
}