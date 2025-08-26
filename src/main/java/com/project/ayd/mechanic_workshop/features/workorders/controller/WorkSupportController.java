package com.project.ayd.mechanic_workshop.features.workorders.controller;

import com.project.ayd.mechanic_workshop.features.workorders.dto.UpdateWorkSupportRequest;
import com.project.ayd.mechanic_workshop.features.workorders.dto.WorkSupportRequest;
import com.project.ayd.mechanic_workshop.features.workorders.dto.WorkSupportResponse;
import com.project.ayd.mechanic_workshop.features.workorders.service.WorkSupportService;
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
import java.util.Map;

@RestController
@RequestMapping("/workorders/support")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO', 'ESPECIALISTA')")
public class WorkSupportController {

    private final WorkSupportService workSupportService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO')")
    public ResponseEntity<WorkSupportResponse> requestSupport(@Valid @RequestBody WorkSupportRequest request) {
        WorkSupportResponse response = workSupportService.requestSupport(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{supportId}")
    public ResponseEntity<WorkSupportResponse> getSupportById(@PathVariable Long supportId) {
        WorkSupportResponse response = workSupportService.getSupportById(supportId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/work/{workId}")
    public ResponseEntity<List<WorkSupportResponse>> getSupportByWorkId(@PathVariable Long workId) {
        List<WorkSupportResponse> response = workSupportService.getSupportByWorkId(workId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or #employeeId == authentication.principal.id")
    public ResponseEntity<Page<WorkSupportResponse>> getSupportRequestsByEmployee(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<WorkSupportResponse> response = workSupportService.getSupportRequestsByEmployee(employeeId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/specialist/{specialistId}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or (#specialistId == authentication.principal.id and hasRole('ESPECIALISTA'))")
    public ResponseEntity<Page<WorkSupportResponse>> getSupportRequestsForSpecialist(
            @PathVariable Long specialistId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<WorkSupportResponse> response = workSupportService.getSupportRequestsForSpecialist(specialistId, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{supportId}/assign/{specialistId}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<WorkSupportResponse> assignSpecialist(
            @PathVariable Long supportId,
            @PathVariable Long specialistId) {
        WorkSupportResponse response = workSupportService.assignSpecialist(supportId, specialistId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{supportId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ESPECIALISTA')")
    public ResponseEntity<WorkSupportResponse> updateSupport(
            @PathVariable Long supportId,
            @Valid @RequestBody UpdateWorkSupportRequest request) {
        WorkSupportResponse response = workSupportService.updateSupport(supportId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{supportId}/start")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ESPECIALISTA')")
    public ResponseEntity<WorkSupportResponse> startSupport(@PathVariable Long supportId) {
        WorkSupportResponse response = workSupportService.startSupport(supportId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{supportId}/complete")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ESPECIALISTA')")
    public ResponseEntity<WorkSupportResponse> completeSupport(@PathVariable Long supportId) {
        WorkSupportResponse response = workSupportService.completeSupport(supportId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{supportId}/cancel")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<WorkSupportResponse> cancelSupport(@PathVariable Long supportId) {
        WorkSupportResponse response = workSupportService.cancelSupport(supportId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Page<WorkSupportResponse>> getSupportByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("urgencyLevel").descending()
                .and(Sort.by("createdAt").ascending()));
        Page<WorkSupportResponse> response = workSupportService.getSupportByStatus(status, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/specialization/{specializationId}/pending")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<WorkSupportResponse>> getPendingSupportBySpecialization(
            @PathVariable Long specializationId) {
        List<WorkSupportResponse> response = workSupportService.getPendingSupportBySpecialization(specializationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unassigned")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<WorkSupportResponse>> getUnassignedPendingSupports() {
        List<WorkSupportResponse> response = workSupportService.getUnassignedPendingSupports();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Long>> getSupportStatistics() {
        Map<String, Long> statistics = workSupportService.getSupportStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/specialist/{specialistId}/active-count")
    @PreAuthorize("hasRole('ADMINISTRADOR') or (#specialistId == authentication.principal.id and hasRole('ESPECIALISTA'))")
    public ResponseEntity<Map<String, Long>> getActiveSupportCountForSpecialist(@PathVariable Long specialistId) {
        Long count = workSupportService.getActiveSupportCountForSpecialist(specialistId);
        return ResponseEntity.ok(Map.of("activeSupportCount", count));
    }

    @PostMapping("/{supportId}/auto-assign")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<WorkSupportResponse> autoAssignSpecialist(@PathVariable Long supportId) {
        WorkSupportResponse response = workSupportService.findBestSpecialistForSupport(supportId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{supportId}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> deleteSupport(@PathVariable Long supportId) {
        workSupportService.deleteSupport(supportId);
        return ResponseEntity.noContent().build();
    }
}