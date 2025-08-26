package com.project.ayd.mechanic_workshop.features.workorders.dto;

import com.project.ayd.mechanic_workshop.features.users.dto.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderTimelineResponse {

    private Long workOrderId;
    private String vehicleLicensePlate;
    private String serviceTypeName;
    private String currentStatus;
    private Integer priorityLevel;
    private String priorityDisplayName;
    private LocalDateTime createdAt;
    private LocalDateTime assignedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private UserResponse assignedEmployee;
    private UserResponse createdBy;
    private Boolean clientApproved;
    private LocalDateTime clientApprovedAt;
    private List<TimelineEventResponse> events;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimelineEventResponse {
        private String eventType;
        private String description;
        private LocalDateTime timestamp;
        private UserResponse performedBy;
        private String additionalData;
    }
}