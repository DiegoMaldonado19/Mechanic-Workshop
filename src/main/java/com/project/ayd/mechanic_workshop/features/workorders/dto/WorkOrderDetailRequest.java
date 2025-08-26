package com.project.ayd.mechanic_workshop.features.workorders.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderDetailRequest {

    @NotNull(message = "Work order ID is required")
    @Positive(message = "Work order ID must be positive")
    private Long workOrderId;

    @NotBlank(message = "Detail description is required")
    @Size(min = 10, max = 1000, message = "Detail description must be between 10 and 1000 characters")
    private String detailDescription;

    @Size(max = 1000, message = "Technical observations cannot exceed 1000 characters")
    private String technicalObservations;

    @Size(max = 1000, message = "Recommendations cannot exceed 1000 characters")
    private String recommendations;

    private BigDecimal estimatedTime;

    private List<Long> requiredPartIds;

    private List<Long> requiredSpecializationIds;
}