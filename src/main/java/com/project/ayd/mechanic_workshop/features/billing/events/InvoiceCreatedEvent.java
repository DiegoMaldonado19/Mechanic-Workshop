package com.project.ayd.mechanic_workshop.features.billing.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceCreatedEvent {

    private Long invoiceId;
    private Long workId;
    private String workDescription;
    private Long quotationId;
    private String clientCui;
    private String clientName;
    private String vehicleLicensePlate;
    private String vehicleModel;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private LocalDate issuedDate;
    private LocalDate dueDate;
    private String createdByUsername;
    private LocalDateTime createdAt;
}