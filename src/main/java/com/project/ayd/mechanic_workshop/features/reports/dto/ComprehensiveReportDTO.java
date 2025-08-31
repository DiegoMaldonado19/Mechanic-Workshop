package com.project.ayd.mechanic_workshop.features.reports.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ComprehensiveReportDTO {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime generatedAt;

    // Reportes incluidos
    private FinancialReportResponse financialReport;
    private OperationalReportResponse operationalReport;
    private PartUsageReportDTO partUsageReport;
    private EmployeePerformanceReportDTO employeePerformanceReport;
    private VehicleBrandReportDTO vehicleBrandReport;
    private ClientServiceReportDTO clientServiceReport;
    private MaintenanceReportDTO preventiveMaintenanceReport;
    private MaintenanceReportDTO correctiveMaintenanceReport;

    // Resumen ejecutivo
    private ExecutiveSummary executiveSummary;

    @Data
    @Builder
    public static class ExecutiveSummary {
        private String periodDescription;
        private String keyFindings;
        private String recommendations;
        private String performanceHighlights;
        private String areasForImprovement;
    }
}