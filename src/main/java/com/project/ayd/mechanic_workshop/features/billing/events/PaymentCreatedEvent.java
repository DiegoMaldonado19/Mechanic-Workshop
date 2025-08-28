package com.project.ayd.mechanic_workshop.features.billing.events;

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
public class PaymentCreatedEvent {

    private Long paymentId;
    private Long invoiceId;
    private BigDecimal paymentAmount;
    private String paymentMethodName;
    private String clientCui;
    private String clientName;
    private String vehicleLicensePlate;
    private LocalDateTime paymentDate;
    private String receivedByUsername;
    private BigDecimal invoiceTotalAmount;
    private BigDecimal totalAmountPaid;
    private BigDecimal pendingAmount;
    private boolean isFullyPaid;
}