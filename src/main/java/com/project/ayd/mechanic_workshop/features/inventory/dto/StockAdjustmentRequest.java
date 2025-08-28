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
public class StockAdjustmentRequest {

    @NotNull(message = "Part ID is required")
    private Long partId;

    @NotNull(message = "New quantity is required")
    private Integer newQuantity;

    @NotNull(message = "Reason is required")
    private String reason;
}