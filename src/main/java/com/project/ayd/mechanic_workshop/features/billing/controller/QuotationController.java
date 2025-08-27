package com.project.ayd.mechanic_workshop.features.billing.controller;

import com.project.ayd.mechanic_workshop.features.billing.dto.QuotationRequest;
import com.project.ayd.mechanic_workshop.features.billing.dto.QuotationResponse;
import com.project.ayd.mechanic_workshop.features.billing.service.QuotationService;
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

@RestController
@RequestMapping("/quotations")
@RequiredArgsConstructor
public class QuotationController {

    private final QuotationService quotationService;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<QuotationResponse> createQuotation(@Valid @RequestBody QuotationRequest request) {
        QuotationResponse response = quotationService.createQuotation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO') or hasRole('CLIENTE')")
    public ResponseEntity<QuotationResponse> getQuotationById(@PathVariable Long id) {
        QuotationResponse response = quotationService.getQuotationById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/work/{workId}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO') or hasRole('CLIENTE')")
    public ResponseEntity<QuotationResponse> getQuotationByWorkId(@PathVariable Long workId) {
        QuotationResponse response = quotationService.getQuotationByWorkId(workId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<Page<QuotationResponse>> getAllQuotations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<QuotationResponse> response = quotationService.getAllQuotations(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/client/{clientCui}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO') or (#clientCui == authentication.principal.personCui and hasRole('CLIENTE'))")
    public ResponseEntity<Page<QuotationResponse>> getQuotationsByClient(
            @PathVariable String clientCui,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<QuotationResponse> response = quotationService.getQuotationsByClient(clientCui, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/approval-status/{approved}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<Page<QuotationResponse>> getQuotationsByApprovalStatus(
            @PathVariable Boolean approved,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<QuotationResponse> response = quotationService.getQuotationsByApprovalStatus(approved, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<Page<QuotationResponse>> getQuotationsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<QuotationResponse> response = quotationService.getQuotationsByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/created-by/{userId}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or #userId == authentication.principal.id")
    public ResponseEntity<Page<QuotationResponse>> getQuotationsByCreatedBy(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<QuotationResponse> response = quotationService.getQuotationsByCreatedBy(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CLIENTE')")
    public ResponseEntity<QuotationResponse> approveQuotation(@PathVariable Long id) {
        QuotationResponse response = quotationService.approveQuotation(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('CLIENTE')")
    public ResponseEntity<QuotationResponse> rejectQuotation(@PathVariable Long id) {
        QuotationResponse response = quotationService.rejectQuotation(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<QuotationResponse> updateQuotation(
            @PathVariable Long id,
            @Valid @RequestBody QuotationRequest request) {
        QuotationResponse response = quotationService.updateQuotation(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/expired")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<List<QuotationResponse>> getExpiredQuotations() {
        List<QuotationResponse> response = quotationService.getExpiredQuotations();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> deleteQuotation(@PathVariable Long id) {
        quotationService.deleteQuotation(id);
        return ResponseEntity.noContent().build();
    }
}