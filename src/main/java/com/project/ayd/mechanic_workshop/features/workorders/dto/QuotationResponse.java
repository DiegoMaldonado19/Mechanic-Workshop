package com.project.ayd.mechanic_workshop.features.workorders.dto;

import com.project.ayd.mechanic_workshop.features.users.dto.UserResponse;
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
    private BigDecimal totalPartsCost;
    private BigDecimal totalLaborCost;
    private BigDecimal totalAmount;
    private LocalDate validUntil;
    private Boolean clientApproved;
    private LocalDateTime approvedAt;
    private UserResponse createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}