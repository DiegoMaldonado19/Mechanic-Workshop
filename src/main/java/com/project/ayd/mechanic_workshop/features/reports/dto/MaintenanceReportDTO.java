package com.project.ayd.mechanic_workshop.features.reports.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class MaintenanceReportDTO {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String maintenanceType; // "Preventivo" o "Correctivo"
    private List<MaintenanceDetail> maintenanceDetails;
    private MaintenanceSummary summary;

    @Data
    @Builder
    public static class MaintenanceDetail {
        private LocalDate maintenanceDate;
        private Integer totalWorks;
        private BigDecimal averageCost;
        private BigDecimal averageDuration;
        private BigDecimal totalRevenue;
        private Integer completedCount;
    }

    @Data
    @Builder
    public static class MaintenanceSummary {
        private Integer totalMaintenances;
        private BigDecimal totalRevenue;
        private BigDecimal averageCost;
        private BigDecimal averageDuration;
        private BigDecimal completionRate;
    }
}