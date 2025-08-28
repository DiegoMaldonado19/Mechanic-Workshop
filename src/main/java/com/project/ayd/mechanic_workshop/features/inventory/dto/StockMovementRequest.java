package com.project.ayd.mechanic_workshop.features.inventory.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementRequest {

    @NotNull(message = "Part ID is required")
    private Long partId;

    @NotNull(message = "Movement type ID is required")
    private Long movementTypeId;

    @NotNull(message = "Quantity is required")
    private Integer quantity;

    private Long referenceTypeId;

    private Long referenceId;

    private String notes;
}