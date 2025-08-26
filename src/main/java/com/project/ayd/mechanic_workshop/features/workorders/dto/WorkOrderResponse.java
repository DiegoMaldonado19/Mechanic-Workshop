package com.project.ayd.mechanic_workshop.features.workorders.dto;

import com.project.ayd.mechanic_workshop.features.users.dto.UserResponse;
import com.project.ayd.mechanic_workshop.features.vehicles.dto.VehicleResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderResponse {

    private Long id;
    private VehicleResponse vehicle;
    private ServiceTypeResponse serviceType;
    private WorkStatusResponse workStatus;
    private UserResponse assignedEmployee;
    private String problemDescription;
    private BigDecimal estimatedHours;
    private BigDecimal actualHours;
    private BigDecimal estimatedCost;
    private BigDecimal actualCost;
    private Boolean clientApproved;
    private LocalDateTime clientApprovedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Integer priorityLevel;
    private String priorityDisplayName;
    private UserResponse createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<WorkProgressResponse> progressHistory;
    private List<WorkPartResponse> parts;
    private List<QuotationResponse> quotations;
}