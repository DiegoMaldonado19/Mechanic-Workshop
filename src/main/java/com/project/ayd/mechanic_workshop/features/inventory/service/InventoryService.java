package com.project.ayd.mechanic_workshop.features.inventory.service;

import com.project.ayd.mechanic_workshop.features.inventory.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InventoryService {

    InventoryStockResponse getStockByPartId(Long partId);

    Page<InventoryStockResponse> getAllStocks(Pageable pageable);

    Page<InventoryStockResponse> searchStocksByPartName(String searchTerm, Pageable pageable);

    Page<InventoryStockResponse> getStocksByCategory(Long categoryId, Pageable pageable);

    List<InventoryStockResponse> getLowStockItems();

    List<InventoryStockResponse> getOutOfStockItems();

    InventoryStockResponse adjustStock(StockAdjustmentRequest request);

    InventoryStockResponse addStock(InventoryStockRequest request);

    InventoryStockResponse removeStock(InventoryStockRequest request);

    InventoryReportResponse getInventoryReport();

    boolean reserveStock(Long partId, Integer quantity);

    boolean releaseReservedStock(Long partId, Integer quantity);

    boolean confirmStockUsage(Long partId, Integer quantity);
}