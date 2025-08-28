package com.project.ayd.mechanic_workshop.features.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long id;
    private Long invoiceId;
    private String vehicleLicensePlate;
    private String clientName;
    private BigDecimal amount;
    private String paymentMethodName;
    private LocalDateTime paymentDate;
    private String referenceNumber;
    private String notes;
    private String receivedByUsername;
    private LocalDateTime createdAt;
}