package com.project.ayd.mechanic_workshop.features.workorders.controller;

import com.project.ayd.mechanic_workshop.features.workorders.dto.*;
import com.project.ayd.mechanic_workshop.features.workorders.service.WorkOrderService;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/workorders")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO', 'ESPECIALISTA')")
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<WorkOrderResponse> createWorkOrder(@Valid @RequestBody WorkOrderRequest request) {
        WorkOrderResponse response = workOrderService.createWorkOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{workOrderId}")
    public ResponseEntity<WorkOrderResponse> getWorkOrderById(@PathVariable Long workOrderId) {
        WorkOrderResponse response = workOrderService.getWorkOrderById(workOrderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<WorkOrderResponse>> getAllWorkOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<WorkOrderResponse> response = workOrderService.getAllWorkOrders(pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{workOrderId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO')")
    public ResponseEntity<WorkOrderResponse> updateWorkOrder(
            @PathVariable Long workOrderId,
            @Valid @RequestBody UpdateWorkOrderRequest request) {
        WorkOrderResponse response = workOrderService.updateWorkOrder(workOrderId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{workOrderId}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> deleteWorkOrder(@PathVariable Long workOrderId) {
        workOrderService.deleteWorkOrder(workOrderId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{workOrderId}/assign")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<WorkOrderResponse> assignWorkOrder(
            @PathVariable Long workOrderId,
            @Valid @RequestBody AssignWorkOrderRequest request) {
        WorkOrderResponse response = workOrderService.assignWorkOrder(workOrderId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{workOrderId}/start")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO', 'ESPECIALISTA')")
    public ResponseEntity<WorkOrderResponse> startWorkOrder(@PathVariable Long workOrderId) {
        WorkOrderResponse response = workOrderService.startWorkOrder(workOrderId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{workOrderId}/complete")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO', 'ESPECIALISTA')")
    public ResponseEntity<WorkOrderResponse> completeWorkOrder(@PathVariable Long workOrderId) {
        WorkOrderResponse response = workOrderService.completeWorkOrder(workOrderId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{workOrderId}/cancel")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<WorkOrderResponse> cancelWorkOrder(@PathVariable Long workOrderId) {
        WorkOrderResponse response = workOrderService.cancelWorkOrder(workOrderId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{workOrderId}/approve")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CLIENTE')")
    public ResponseEntity<WorkOrderResponse> approveWorkOrder(@PathVariable Long workOrderId) {
        WorkOrderResponse response = workOrderService.approveWorkOrder(workOrderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO', 'ESPECIALISTA')")
    public ResponseEntity<Page<WorkOrderResponse>> getWorkOrdersByEmployee(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<WorkOrderResponse> response = workOrderService.getWorkOrdersByEmployee(employeeId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/client/{clientCui}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CLIENTE')")
    public ResponseEntity<Page<WorkOrderResponse>> getWorkOrdersByClient(
            @PathVariable String clientCui,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<WorkOrderResponse> response = workOrderService.getWorkOrdersByClient(clientCui, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{statusId}")
    public ResponseEntity<Page<WorkOrderResponse>> getWorkOrdersByStatus(
            @PathVariable Long statusId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<WorkOrderResponse> response = workOrderService.getWorkOrdersByStatus(statusId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/service-type/{serviceTypeId}")
    public ResponseEntity<Page<WorkOrderResponse>> getWorkOrdersByServiceType(
            @PathVariable Long serviceTypeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<WorkOrderResponse> response = workOrderService.getWorkOrdersByServiceType(serviceTypeId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vehicle/{vehicleId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CLIENTE')")
    public ResponseEntity<Page<WorkOrderResponse>> getWorkOrdersByVehicle(
            @PathVariable Long vehicleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<WorkOrderResponse> response = workOrderService.getWorkOrdersByVehicle(vehicleId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/priority/{priorityLevel}")
    public ResponseEntity<Page<WorkOrderResponse>> getWorkOrdersByPriority(
            @PathVariable Integer priorityLevel,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<WorkOrderResponse> response = workOrderService.getWorkOrdersByPriority(priorityLevel, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date-range")
    public ResponseEntity<Page<WorkOrderResponse>> getWorkOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<WorkOrderResponse> response = workOrderService.getWorkOrdersByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}/active")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO', 'ESPECIALISTA')")
    public ResponseEntity<List<WorkOrderResponse>> getActiveWorkOrdersByEmployee(@PathVariable Long employeeId) {
        List<WorkOrderResponse> response = workOrderService.getActiveWorkOrdersByEmployee(employeeId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count/status/{statusId}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Long>> countWorkOrdersByStatus(@PathVariable Long statusId) {
        Long count = workOrderService.countWorkOrdersByStatus(statusId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @GetMapping("/count/employee/{employeeId}/active")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO', 'ESPECIALISTA')")
    public ResponseEntity<Map<String, Long>> countActiveWorkOrdersByEmployee(@PathVariable Long employeeId) {
        Long count = workOrderService.countActiveWorkOrdersByEmployee(employeeId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    // Work Progress Endpoints
    @PostMapping("/progress")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO', 'ESPECIALISTA')")
    public ResponseEntity<WorkProgressResponse> addWorkProgress(@Valid @RequestBody WorkProgressRequest request) {
        WorkProgressResponse response = workOrderService.addWorkProgress(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{workOrderId}/progress")
    public ResponseEntity<List<WorkProgressResponse>> getWorkProgressByWorkOrder(@PathVariable Long workOrderId) {
        List<WorkProgressResponse> response = workOrderService.getWorkProgressByWorkOrder(workOrderId);
        return ResponseEntity.ok(response);
    }

    // Work Parts Endpoints
    @PostMapping("/parts")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO', 'ESPECIALISTA')")
    public ResponseEntity<WorkPartResponse> addWorkPart(@Valid @RequestBody WorkPartRequest request) {
        WorkPartResponse response = workOrderService.addWorkPart(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{workOrderId}/parts")
    public ResponseEntity<List<WorkPartResponse>> getWorkPartsByWorkOrder(@PathVariable Long workOrderId) {
        List<WorkPartResponse> response = workOrderService.getWorkPartsByWorkOrder(workOrderId);
        return ResponseEntity.ok(response);
    }

    // Quotations Endpoints
    @PostMapping("/quotations")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<QuotationResponse> createQuotation(@Valid @RequestBody QuotationRequest request) {
        QuotationResponse response = workOrderService.createQuotation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{workOrderId}/quotations")
    public ResponseEntity<List<QuotationResponse>> getQuotationsByWorkOrder(@PathVariable Long workOrderId) {
        List<QuotationResponse> response = workOrderService.getQuotationsByWorkOrder(workOrderId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/quotations/{quotationId}/approve")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CLIENTE')")
    public ResponseEntity<QuotationResponse> approveQuotation(@PathVariable Long quotationId) {
        QuotationResponse response = workOrderService.approveQuotation(quotationId);
        return ResponseEntity.ok(response);
    }

    // Reference Data Endpoints
    @GetMapping("/service-types")
    public ResponseEntity<List<ServiceTypeResponse>> getAllServiceTypes() {
        List<ServiceTypeResponse> response = workOrderService.getAllServiceTypes();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/work-statuses")
    public ResponseEntity<List<WorkStatusResponse>> getAllWorkStatuses() {
        List<WorkStatusResponse> response = workOrderService.getAllWorkStatuses();
        return ResponseEntity.ok(response);
    }
}