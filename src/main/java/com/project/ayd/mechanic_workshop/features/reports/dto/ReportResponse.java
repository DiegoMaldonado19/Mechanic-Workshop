package com.project.ayd.mechanic_workshop.features.reports.dto;

import com.project.ayd.mechanic_workshop.features.reports.enums.ReportFormat;
import com.project.ayd.mechanic_workshop.features.reports.enums.ReportType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReportResponse {
    private String reportId;
    private ReportType reportType;
    private ReportFormat format;
    private String fileName;
    private String downloadUrl;
    private LocalDateTime generatedAt;
    private String generatedBy;
    private Long fileSize;
    private LocalDateTime expiresAt;
    private String status;
}