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
public class AdditionalServiceResponse {

    private Long id;
    private Long workId;
    private String workDescription;
    private String vehicleLicensePlate;
    private ServiceTypeResponse serviceType;
    private UserResponse requestedBy;
    private String description;
    private String justification;
    private BigDecimal estimatedHours;
    private BigDecimal estimatedCost;
    private Integer urgencyLevel;
    private String urgencyDisplayName;
    private String status;
    private Boolean clientApproved;
    private LocalDateTime clientApprovedAt;
    private UserResponse approvedBy;
    private String approvalNotes;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}