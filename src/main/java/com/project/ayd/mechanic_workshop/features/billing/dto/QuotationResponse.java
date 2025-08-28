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
public class QuotationResponse {

    private Long id;
    private Long workId;
    private String workDescription;
    private String vehicleLicensePlate;
    private String vehicleModel;
    private String clientName;
    private BigDecimal totalPartsCost;
    private BigDecimal totalLaborCost;
    private BigDecimal totalAmount;
    private LocalDate validUntil;
    private Boolean clientApproved;
    private LocalDateTime approvedAt;
    private String createdByUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}