package com.project.ayd.mechanic_workshop.features.inventory.controller;

import com.project.ayd.mechanic_workshop.features.inventory.dto.*;
import com.project.ayd.mechanic_workshop.features.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO')")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/stock/{partId}")
    public ResponseEntity<InventoryStockResponse> getStockByPartId(@PathVariable Long partId) {
        InventoryStockResponse response = inventoryService.getStockByPartId(partId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stock")
    public ResponseEntity<Page<InventoryStockResponse>> getAllStocks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "part.name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<InventoryStockResponse> response = inventoryService.getAllStocks(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stock/search")
    public ResponseEntity<Page<InventoryStockResponse>> searchStocksByPartName(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("part.name").ascending());
        Page<InventoryStockResponse> response = inventoryService.searchStocksByPartName(searchTerm, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stock/category/{categoryId}")
    public ResponseEntity<Page<InventoryStockResponse>> getStocksByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("part.name").ascending());
        Page<InventoryStockResponse> response = inventoryService.getStocksByCategory(categoryId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stock/low")
    public ResponseEntity<List<InventoryStockResponse>> getLowStockItems() {
        List<InventoryStockResponse> response = inventoryService.getLowStockItems();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stock/out")
    public ResponseEntity<List<InventoryStockResponse>> getOutOfStockItems() {
        List<InventoryStockResponse> response = inventoryService.getOutOfStockItems();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/stock/adjust")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<InventoryStockResponse> adjustStock(@Valid @RequestBody StockAdjustmentRequest request) {
        InventoryStockResponse response = inventoryService.adjustStock(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/stock/add")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<InventoryStockResponse> addStock(@Valid @RequestBody InventoryStockRequest request) {
        InventoryStockResponse response = inventoryService.addStock(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/stock/remove")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<InventoryStockResponse> removeStock(@Valid @RequestBody InventoryStockRequest request) {
        InventoryStockResponse response = inventoryService.removeStock(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/report")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<InventoryReportResponse> getInventoryReport() {
        InventoryReportResponse response = inventoryService.getInventoryReport();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/stock/reserve/{partId}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> reserveStock(@PathVariable Long partId, @RequestParam Integer quantity) {
        boolean success = inventoryService.reserveStock(partId, quantity);
        return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @PostMapping("/stock/release/{partId}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> releaseReservedStock(@PathVariable Long partId, @RequestParam Integer quantity) {
        boolean success = inventoryService.releaseReservedStock(partId, quantity);
        return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @PostMapping("/stock/confirm/{partId}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> confirmStockUsage(@PathVariable Long partId, @RequestParam Integer quantity) {
        boolean success = inventoryService.confirmStockUsage(partId, quantity);
        return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
}