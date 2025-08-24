package com.project.ayd.mechanic_workshop.features.vehicles.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehicleHistoryResponse {
    private Long vehicleId;
    private String licensePlate;
    private List<WorkHistoryItem> workHistory;
    private BigDecimal totalSpent;
    private Integer totalWorks;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WorkHistoryItem {
        private Long workId;
        private String serviceType;
        private String status;
        private String description;
        private BigDecimal cost;
        private LocalDateTime createdAt;
        private LocalDateTime completedAt;
        private String assignedEmployee;
    }
}