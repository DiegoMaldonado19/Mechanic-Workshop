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
public class AdditionalServiceRequestDto {

    @NotNull(message = "Work ID is required")
    @Positive(message = "Work ID must be positive")
    private Long workId;

    @NotNull(message = "Service type ID is required")
    @Positive(message = "Service type ID must be positive")
    private Long serviceTypeId;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    private String description;

    @Size(max = 1000, message = "Justification cannot exceed 1000 characters")
    private String justification;

    @DecimalMin(value = "0.1", message = "Estimated hours must be greater than 0")
    @DecimalMax(value = "999.99", message = "Estimated hours cannot exceed 999.99")
    @Digits(integer = 3, fraction = 2, message = "Estimated hours must have at most 3 integer digits and 2 decimal places")
    private BigDecimal estimatedHours;

    @DecimalMin(value = "0.01", message = "Estimated cost must be greater than 0")
    @DecimalMax(value = "99999999.99", message = "Estimated cost cannot exceed 99999999.99")
    @Digits(integer = 8, fraction = 2, message = "Estimated cost must have at most 8 integer digits and 2 decimal places")
    private BigDecimal estimatedCost;

    @Min(value = 1, message = "Urgency level must be at least 1")
    @Max(value = 5, message = "Urgency level cannot exceed 5")
    @Builder.Default
    private Integer urgencyLevel = 1;
}