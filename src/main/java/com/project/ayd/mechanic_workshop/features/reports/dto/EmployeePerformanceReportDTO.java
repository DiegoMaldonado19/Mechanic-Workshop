package com.project.ayd.mechanic_workshop.features.reports.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class EmployeePerformanceReportDTO {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<EmployeePerformanceDetail> employeeDetails;
    private PerformanceSummary summary;

    @Data
    @Builder
    public static class EmployeePerformanceDetail {
        private String employeeName;
        private Long employeeId;
        private Integer totalWorks;
        private Integer completedWorks;
        private BigDecimal averageWorkTime;
        private BigDecimal totalRevenue;
        private BigDecimal completionRate;
    }

    @Data
    @Builder
    public static class PerformanceSummary {
        private Integer totalEmployees;
        private BigDecimal totalRevenue;
        private BigDecimal averageCompletionRate;
        private String topPerformer;
    }
}