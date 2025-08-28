package com.project.ayd.mechanic_workshop.features.reports.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class OperationalReportResponse {
    private String reportPeriod;
    private LocalDate startDate;
    private LocalDate endDate;

    // Métricas de trabajos
    private Long totalWorks;
    private Long completedWorks;
    private Long pendingWorks;
    private Long cancelledWorks;
    private BigDecimal averageWorkDuration;
    private BigDecimal workCompletionRate;

    // Métricas de empleados
    private List<EmployeePerformance> employeePerformances;
    private List<WorkTypeStatistics> workTypeStats;
    private List<VehicleBrandStatistics> vehicleBrandStats;
    private List<PartUsageStatistics> partUsageStats;

    @Data
    @Builder
    public static class EmployeePerformance {
        private String employeeName;
        private String employeeId;
        private Long totalWorksAssigned;
        private Long completedWorks;
        private BigDecimal averageWorkTime;
        private BigDecimal completionRate;
        private BigDecimal totalRevenue;
    }

    @Data
    @Builder
    public static class WorkTypeStatistics {
        private String serviceTypeName;
        private Long totalWorks;
        private BigDecimal averageCost;
        private BigDecimal averageDuration;
        private BigDecimal totalRevenue;
    }

    @Data
    @Builder
    public static class VehicleBrandStatistics {
        private String brandName;
        private Long totalWorks;
        private BigDecimal averageCost;
        private Long uniqueVehicles;
    }

    @Data
    @Builder
    public static class PartUsageStatistics {
        private String partName;
        private String partCategory;
        private Long totalQuantityUsed;
        private BigDecimal totalCost;
        private Long worksUsedIn;
    }
}