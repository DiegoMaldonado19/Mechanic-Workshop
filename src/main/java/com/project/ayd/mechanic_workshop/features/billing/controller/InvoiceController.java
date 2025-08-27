package com.project.ayd.mechanic_workshop.features.billing.controller;

import com.project.ayd.mechanic_workshop.features.billing.dto.InvoiceRequest;
import com.project.ayd.mechanic_workshop.features.billing.dto.InvoiceResponse;
import com.project.ayd.mechanic_workshop.features.billing.dto.BillingSummaryResponse;
import com.project.ayd.mechanic_workshop.features.billing.service.InvoiceService;
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

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<InvoiceResponse> createInvoice(@Valid @RequestBody InvoiceRequest request) {
        InvoiceResponse response = invoiceService.createInvoice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO') or hasRole('CLIENTE')")
    public ResponseEntity<InvoiceResponse> getInvoiceById(@PathVariable Long id) {
        InvoiceResponse response = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/work/{workId}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO') or hasRole('CLIENTE')")
    public ResponseEntity<InvoiceResponse> getInvoiceByWorkId(@PathVariable Long workId) {
        InvoiceResponse response = invoiceService.getInvoiceByWorkId(workId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<Page<InvoiceResponse>> getAllInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<InvoiceResponse> response = invoiceService.getAllInvoices(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/client/{clientCui}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO') or (#clientCui == authentication.principal.personCui and hasRole('CLIENTE'))")
    public ResponseEntity<Page<InvoiceResponse>> getInvoicesByClient(
            @PathVariable String clientCui,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<InvoiceResponse> response = invoiceService.getInvoicesByClient(clientCui, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{statusName}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<Page<InvoiceResponse>> getInvoicesByPaymentStatus(
            @PathVariable String statusName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<InvoiceResponse> response = invoiceService.getInvoicesByPaymentStatus(statusName, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<Page<InvoiceResponse>> getInvoicesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("issuedDate").descending());
        Page<InvoiceResponse> response = invoiceService.getInvoicesByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/created-by/{userId}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or #userId == authentication.principal.id")
    public ResponseEntity<Page<InvoiceResponse>> getInvoicesByCreatedBy(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<InvoiceResponse> response = invoiceService.getInvoicesByCreatedBy(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<List<InvoiceResponse>> getOverdueInvoices() {
        List<InvoiceResponse> response = invoiceService.getOverdueInvoices();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<BillingSummaryResponse> getBillingSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        BillingSummaryResponse response = invoiceService.getBillingSummary(startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<InvoiceResponse> updateInvoice(
            @PathVariable Long id,
            @Valid @RequestBody InvoiceRequest request) {
        InvoiceResponse response = invoiceService.updateInvoice(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }
}