package com.project.ayd.mechanic_workshop.features.inventory.controller;

import com.project.ayd.mechanic_workshop.features.inventory.dto.*;
import com.project.ayd.mechanic_workshop.features.inventory.service.PurchaseOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inventory/purchase-orders")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO')")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<PurchaseOrderResponse> createPurchaseOrder(@Valid @RequestBody PurchaseOrderRequest request) {
        PurchaseOrderResponse response = purchaseOrderService.createPurchaseOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{purchaseOrderId}")
    public ResponseEntity<PurchaseOrderResponse> getPurchaseOrderById(@PathVariable Long purchaseOrderId) {
        PurchaseOrderResponse response = purchaseOrderService.getPurchaseOrderById(purchaseOrderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<PurchaseOrderResponse>> getAllPurchaseOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PurchaseOrderResponse> response = purchaseOrderService.getAllPurchaseOrders(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<Page<PurchaseOrderResponse>> getPurchaseOrdersBySupplier(
            @PathVariable Long supplierId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PurchaseOrderResponse> response = purchaseOrderService.getPurchaseOrdersBySupplier(supplierId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{statusId}")
    public ResponseEntity<Page<PurchaseOrderResponse>> getPurchaseOrdersByStatus(
            @PathVariable Long statusId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PurchaseOrderResponse> response = purchaseOrderService.getPurchaseOrdersByStatus(statusId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date-range")
    public ResponseEntity<Page<PurchaseOrderResponse>> getPurchaseOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        Page<PurchaseOrderResponse> response = purchaseOrderService.getPurchaseOrdersByDateRange(startDate, endDate,
                pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{purchaseOrderId}/status/{statusId}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<PurchaseOrderResponse> updatePurchaseOrderStatus(
            @PathVariable Long purchaseOrderId,
            @PathVariable Long statusId) {
        PurchaseOrderResponse response = purchaseOrderService.updatePurchaseOrderStatus(purchaseOrderId, statusId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{purchaseOrderId}/items/{itemId}/receive")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<PurchaseOrderResponse> receivePurchaseOrderItem(
            @PathVariable Long purchaseOrderId,
            @PathVariable Long itemId,
            @RequestParam Integer quantityReceived) {
        PurchaseOrderResponse response = purchaseOrderService.receivePurchaseOrderItem(purchaseOrderId, itemId,
                quantityReceived);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{purchaseOrderId}/complete-delivery")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<PurchaseOrderResponse> completePurchaseOrderDelivery(@PathVariable Long purchaseOrderId) {
        PurchaseOrderResponse response = purchaseOrderService.completePurchaseOrderDelivery(purchaseOrderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/items/pending")
    public ResponseEntity<List<PurchaseOrderItemResponse>> getPendingItems() {
        List<PurchaseOrderItemResponse> response = purchaseOrderService.getPendingItems();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/items/pending/part/{partId}")
    public ResponseEntity<Map<String, Integer>> getPendingQuantityForPart(@PathVariable Long partId) {
        Integer pendingQuantity = purchaseOrderService.getPendingQuantityForPart(partId);
        return ResponseEntity.ok(Map.of("pendingQuantity", pendingQuantity));
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<PurchaseOrderStatusResponse>> getAllPurchaseOrderStatuses() {
        List<PurchaseOrderStatusResponse> response = purchaseOrderService.getAllPurchaseOrderStatuses();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count/status/{statusName}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Long>> countPurchaseOrdersByStatus(@PathVariable String statusName) {
        Long count = purchaseOrderService.countPurchaseOrdersByStatus(statusName);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @GetMapping("/total-amount/period")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Double>> getTotalPurchaseAmountInPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Double totalAmount = purchaseOrderService.getTotalPurchaseAmountInPeriod(startDate, endDate);
        return ResponseEntity.ok(Map.of("totalAmount", totalAmount));
    }
}