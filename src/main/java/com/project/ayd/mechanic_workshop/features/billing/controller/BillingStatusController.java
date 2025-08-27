package com.project.ayd.mechanic_workshop.features.billing.controller;

import com.project.ayd.mechanic_workshop.features.billing.dto.PaymentStatusResponse;
import com.project.ayd.mechanic_workshop.features.billing.dto.PaymentMethodResponse;
import com.project.ayd.mechanic_workshop.features.billing.service.BillingStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/billing")
@RequiredArgsConstructor
public class BillingStatusController {

    private final BillingStatusService billingStatusService;

    @GetMapping("/payment-statuses")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO') or hasRole('CLIENTE')")
    public ResponseEntity<List<PaymentStatusResponse>> getAllPaymentStatuses() {
        List<PaymentStatusResponse> response = billingStatusService.getAllPaymentStatuses();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/payment-methods")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO') or hasRole('CLIENTE')")
    public ResponseEntity<List<PaymentMethodResponse>> getAllPaymentMethods() {
        List<PaymentMethodResponse> response = billingStatusService.getAllPaymentMethods();
        return ResponseEntity.ok(response);
    }
}