package com.project.ayd.mechanic_workshop.features.workorders.controller;

import com.project.ayd.mechanic_workshop.features.workorders.dto.AdditionalServiceRequestDto;
import com.project.ayd.mechanic_workshop.features.workorders.dto.AdditionalServiceResponse;
import com.project.ayd.mechanic_workshop.features.workorders.dto.ApproveAdditionalServiceRequest;
import com.project.ayd.mechanic_workshop.features.workorders.service.AdditionalServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/workorders/additional-services")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO', 'ESPECIALISTA', 'CLIENTE')")
public class AdditionalServiceController {

    private final AdditionalServiceService additionalServiceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO', 'ESPECIALISTA')")
    public ResponseEntity<AdditionalServiceResponse> requestAdditionalService(
            @Valid @RequestBody AdditionalServiceRequestDto request) {
        AdditionalServiceResponse response = additionalServiceService.requestAdditionalService(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<AdditionalServiceResponse> getAdditionalServiceById(@PathVariable Long requestId) {
        AdditionalServiceResponse response = additionalServiceService.getAdditionalServiceById(requestId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/work/{workId}")
    public ResponseEntity<List<AdditionalServiceResponse>> getAdditionalServicesByWork(@PathVariable Long workId) {
        List<AdditionalServiceResponse> response = additionalServiceService.getAdditionalServicesByWork(workId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or (#employeeId == authentication.principal.id and hasAnyRole('EMPLEADO', 'ESPECIALISTA'))")
    public ResponseEntity<Page<AdditionalServiceResponse>> getAdditionalServicesByEmployee(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AdditionalServiceResponse> response = additionalServiceService.getAdditionalServicesByEmployee(employeeId,
                pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/client/{clientCui}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or #clientCui == authentication.principal.username")
    public ResponseEntity<Page<AdditionalServiceResponse>> getAdditionalServicesByClient(
            @PathVariable String clientCui,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AdditionalServiceResponse> response = additionalServiceService.getAdditionalServicesByClient(clientCui,
                pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-requests")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Page<AdditionalServiceResponse>> getMyAdditionalServices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String currentUserCui = SecurityContextHolder.getContext().getAuthentication().getName();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AdditionalServiceResponse> response = additionalServiceService
                .getAdditionalServicesByClient(currentUserCui, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Page<AdditionalServiceResponse>> getAdditionalServicesByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("urgencyLevel").descending()
                .and(Sort.by("createdAt").ascending()));
        Page<AdditionalServiceResponse> response = additionalServiceService.getAdditionalServicesByStatus(status,
                pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending-client-approval")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<AdditionalServiceResponse>> getPendingClientApproval() {
        String currentUserCui = SecurityContextHolder.getContext().getAuthentication().getName();
        List<AdditionalServiceResponse> response = additionalServiceService.getPendingClientApproval(currentUserCui);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{requestId}/approve-reject")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<AdditionalServiceResponse> approveOrRejectService(
            @PathVariable Long requestId,
            @Valid @RequestBody ApproveAdditionalServiceRequest request) {
        AdditionalServiceResponse response = additionalServiceService.approveOrRejectService(requestId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{requestId}/client-approve")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<AdditionalServiceResponse> clientApproveService(@PathVariable Long requestId) {
        AdditionalServiceResponse response = additionalServiceService.clientApproveService(requestId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{requestId}/client-reject")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<AdditionalServiceResponse> clientRejectService(
            @PathVariable Long requestId,
            @RequestParam(required = false) String reason) {
        AdditionalServiceResponse response = additionalServiceService.clientRejectService(requestId, reason);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{requestId}/start")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO', 'ESPECIALISTA')")
    public ResponseEntity<AdditionalServiceResponse> startAdditionalService(@PathVariable Long requestId) {
        AdditionalServiceResponse response = additionalServiceService.startAdditionalService(requestId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{requestId}/complete")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO', 'ESPECIALISTA')")
    public ResponseEntity<AdditionalServiceResponse> completeAdditionalService(@PathVariable Long requestId) {
        AdditionalServiceResponse response = additionalServiceService.completeAdditionalService(requestId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/approved-pending-client")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<AdditionalServiceResponse>> getApprovedButNotClientApproved() {
        List<AdditionalServiceResponse> response = additionalServiceService.getApprovedButNotClientApproved();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Long>> getAdditionalServiceStatistics() {
        Map<String, Long> statistics = additionalServiceService.getAdditionalServiceStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/work/{workId}/count")
    public ResponseEntity<Map<String, Long>> getAdditionalServiceCountByWork(@PathVariable Long workId) {
        Long count = additionalServiceService.getAdditionalServiceCountByWork(workId);
        return ResponseEntity.ok(Map.of("additionalServiceCount", count));
    }

    @DeleteMapping("/{requestId}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> deleteAdditionalServiceRequest(@PathVariable Long requestId) {
        additionalServiceService.deleteAdditionalServiceRequest(requestId);
        return ResponseEntity.noContent().build();
    }
}