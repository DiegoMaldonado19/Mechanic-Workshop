package com.project.ayd.mechanic_workshop.features.reports.dto;

import com.project.ayd.mechanic_workshop.features.reports.enums.ReportFormat;
import com.project.ayd.mechanic_workshop.features.reports.enums.ReportPeriod;
import com.project.ayd.mechanic_workshop.features.reports.enums.ReportType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ReportRequest {
    @NotNull(message = "Report type is required")
    private ReportType reportType;

    @NotNull(message = "Report format is required")
    private ReportFormat format;

    private ReportPeriod period;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private Map<String, Object> parameters;
}