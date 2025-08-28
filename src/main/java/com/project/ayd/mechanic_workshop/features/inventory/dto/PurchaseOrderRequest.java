package com.project.ayd.mechanic_workshop.features.inventory.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderRequest {

    @NotNull(message = "Supplier ID is required")
    private Long supplierId;

    private LocalDate expectedDeliveryDate;

    @NotEmpty(message = "Purchase order must have at least one item")
    @Valid
    private List<PurchaseOrderItemRequest> items;
}