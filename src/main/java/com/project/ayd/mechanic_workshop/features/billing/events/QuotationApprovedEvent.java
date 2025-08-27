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
public class QuotationApprovedEvent {

    private Long quotationId;
    private Long workId;
    private String workDescription;
    private String clientCui;
    private String clientName;
    private String vehicleLicensePlate;
    private String vehicleModel;
    private BigDecimal totalPartsCost;
    private BigDecimal totalLaborCost;
    private BigDecimal totalAmount;
    private LocalDate validUntil;
    private LocalDateTime approvedAt;
    private String createdByUsername;
}