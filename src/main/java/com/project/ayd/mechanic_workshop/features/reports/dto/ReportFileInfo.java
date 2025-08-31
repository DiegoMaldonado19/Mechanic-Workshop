package com.project.ayd.mechanic_workshop.features.reports.dto;

import com.project.ayd.mechanic_workshop.features.reports.enums.ReportFormat;
import com.project.ayd.mechanic_workshop.features.reports.enums.ReportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportFileInfo {
    private String reportId;
    private String fileName;
    private ReportType reportType;
    private ReportFormat format;
    private Long fileSize;
    private LocalDateTime generatedAt;
    private LocalDateTime expiresAt;
    private String generatedBy;
    private String status;
    private String downloadUrl;
    private boolean isExpired;
    private String fileSizeFormatted;
    private String reportTypeDisplayName;
    private String formatDisplayName;
}