package com.project.ayd.mechanic_workshop.features.billing.dto;

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
public class InvoiceResponse {

    private Long id;
    private Long workId;
    private String workDescription;
    private Long quotationId;
    private String vehicleLicensePlate;
    private String vehicleModel;
    private String clientName;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private LocalDate issuedDate;
    private LocalDate dueDate;
    private String paymentStatus;
    private BigDecimal amountPaid;
    private BigDecimal pendingAmount;
    private String createdByUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}