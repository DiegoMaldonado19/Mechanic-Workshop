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
public class WorkProgressRequest {

    @NotNull(message = "Work ID is required")
    @Positive(message = "Work ID must be positive")
    private Long workId;

    @NotBlank(message = "Progress description is required")
    @Size(min = 10, max = 1000, message = "Progress description must be between 10 and 1000 characters")
    private String progressDescription;

    @DecimalMin(value = "0.1", message = "Hours worked must be greater than 0")
    @DecimalMax(value = "24.0", message = "Hours worked cannot exceed 24 hours per entry")
    @Digits(integer = 2, fraction = 2, message = "Hours worked must have at most 2 integer digits and 2 decimal places")
    private BigDecimal hoursWorked;

    @Size(max = 1000, message = "Observations cannot exceed 1000 characters")
    private String observations;

    @Size(max = 1000, message = "Symptoms detected cannot exceed 1000 characters")
    private String symptomsDetected;

    @Size(max = 1000, message = "Additional damage found cannot exceed 1000 characters")
    private String additionalDamageFound;
}