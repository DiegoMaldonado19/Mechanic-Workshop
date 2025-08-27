package com.project.ayd.mechanic_workshop.features.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReportResponse {

    private Long totalParts;
    private Long totalStockQuantity;
    private Long lowStockCount;
    private Long outOfStockCount;
    private BigDecimal totalInventoryValue;
    private Long totalSuppliers;
    private Long pendingPurchaseOrders;
    private BigDecimal pendingPurchaseValue;
}