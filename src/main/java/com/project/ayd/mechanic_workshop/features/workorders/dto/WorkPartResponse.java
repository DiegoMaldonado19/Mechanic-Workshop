package com.project.ayd.mechanic_workshop.features.workorders.dto;

import com.project.ayd.mechanic_workshop.features.users.dto.UserResponse;
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
public class WorkPartResponse {

    private Long id;
    private Long workId;
    private PartResponse part;
    private Integer quantityNeeded;
    private Integer quantityUsed;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private UserResponse requestedBy;
    private UserResponse approvedBy;
    private LocalDateTime createdAt;
}