package com.project.ayd.mechanic_workshop.features.billing.controller;

import com.project.ayd.mechanic_workshop.features.billing.dto.PaymentRequest;
import com.project.ayd.mechanic_workshop.features.billing.dto.PaymentResponse;
import com.project.ayd.mechanic_workshop.features.billing.dto.PaymentMethodResponse;
import com.project.ayd.mechanic_workshop.features.billing.service.PaymentService;
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
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO') or hasRole('CLIENTE')")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id) {
        PaymentResponse response = paymentService.getPaymentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/invoice/{invoiceId}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO') or hasRole('CLIENTE')")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByInvoiceId(@PathVariable Long invoiceId) {
        List<PaymentResponse> response = paymentService.getPaymentsByInvoiceId(invoiceId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<Page<PaymentResponse>> getAllPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "paymentDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PaymentResponse> response = paymentService.getAllPayments(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/client/{clientCui}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO') or (#clientCui == authentication.principal.personCui and hasRole('CLIENTE'))")
    public ResponseEntity<Page<PaymentResponse>> getPaymentsByClient(
            @PathVariable String clientCui,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("paymentDate").descending());
        Page<PaymentResponse> response = paymentService.getPaymentsByClient(clientCui, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/method/{methodId}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<Page<PaymentResponse>> getPaymentsByMethod(
            @PathVariable Long methodId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("paymentDate").descending());
        Page<PaymentResponse> response = paymentService.getPaymentsByMethod(methodId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<Page<PaymentResponse>> getPaymentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("paymentDate").descending());
        Page<PaymentResponse> response = paymentService.getPaymentsByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/received-by/{userId}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or #userId == authentication.principal.id")
    public ResponseEntity<Page<PaymentResponse>> getPaymentsByReceivedBy(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("paymentDate").descending());
        Page<PaymentResponse> response = paymentService.getPaymentsByReceivedBy(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/methods")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO') or hasRole('CLIENTE')")
    public ResponseEntity<List<PaymentMethodResponse>> getAllPaymentMethods() {
        List<PaymentMethodResponse> response = paymentService.getAllPaymentMethods();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}