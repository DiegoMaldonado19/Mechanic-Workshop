package com.project.ayd.mechanic_workshop.features.reports.service;

import com.project.ayd.mechanic_workshop.features.reports.dto.ReportRequest;
import com.project.ayd.mechanic_workshop.features.reports.dto.ReportResponse;
import com.project.ayd.mechanic_workshop.features.reports.dto.FinancialReportResponse;
import com.project.ayd.mechanic_workshop.features.reports.dto.OperationalReportResponse;
import com.project.ayd.mechanic_workshop.features.reports.enums.ReportType;
import org.springframework.core.io.Resource;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportService {

    ReportResponse generateReport(ReportRequest request);

    Resource downloadReport(String reportId);

    List<ReportResponse> getReportHistory(String username);

    void deleteExpiredReports();

    FinancialReportResponse generateFinancialReport(LocalDateTime startDate, LocalDateTime endDate);

    OperationalReportResponse generateOperationalReport(LocalDateTime startDate, LocalDateTime endDate);

    List<Object[]> getIncomeByWeek(LocalDateTime startDate, LocalDateTime endDate);

    List<Object[]> getPreventiveMaintenanceReport(LocalDateTime startDate, LocalDateTime endDate);

    List<Object[]> getPartsByVehicleBrand(LocalDateTime startDate, LocalDateTime endDate);

    List<Object[]> getClientHistoryReport(LocalDateTime startDate, LocalDateTime endDate);

    byte[] exportReportData(ReportType reportType, LocalDateTime startDate,
            LocalDateTime endDate, String format);
}