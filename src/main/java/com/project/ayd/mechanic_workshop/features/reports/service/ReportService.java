package com.project.ayd.mechanic_workshop.features.reports.service;

import com.project.ayd.mechanic_workshop.features.reports.dto.ReportRequest;
import com.project.ayd.mechanic_workshop.features.reports.dto.ReportResponse;
import com.project.ayd.mechanic_workshop.features.reports.dto.FinancialReportResponse;
import com.project.ayd.mechanic_workshop.features.reports.dto.OperationalReportResponse;
import com.project.ayd.mechanic_workshop.features.reports.dto.ReportFileInfo;
import com.project.ayd.mechanic_workshop.features.reports.enums.ReportType;
import org.springframework.core.io.Resource;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportService {

    // ================================
    // CORE REPORT METHODS
    // ================================

    ReportResponse generateReport(ReportRequest request);

    Resource downloadReport(String reportId);

    List<ReportResponse> getReportHistory(String username);

    void deleteExpiredReports();

    byte[] exportReportData(ReportType reportType, LocalDateTime startDate,
            LocalDateTime endDate, String format);

    // ================================
    // CONSOLIDATED REPORTS
    // ================================

    FinancialReportResponse generateFinancialReport(LocalDateTime startDate, LocalDateTime endDate);

    OperationalReportResponse generateOperationalReport(LocalDateTime startDate, LocalDateTime endDate);

    // ================================
    // FINANCIAL REPORTS
    // ================================

    List<Object[]> getIncomeByWeek(LocalDateTime startDate, LocalDateTime endDate);

    List<Object[]> getIncomeByMonth(LocalDateTime startDate, LocalDateTime endDate);

    List<Object[]> getExpensesByMonth(LocalDateTime startDate, LocalDateTime endDate);

    List<Object[]> getProviderExpenses(LocalDateTime startDate, LocalDateTime endDate);

    // ================================
    // OPERATIONAL REPORTS
    // ================================

    List<Object[]> getWorksByDateAndType(LocalDateTime startDate, LocalDateTime endDate);

    List<Object[]> getWorksByEmployee(LocalDateTime startDate, LocalDateTime endDate);

    List<Object[]> getVehicleMaintenanceHistory(String licensePlate, LocalDateTime startDate, LocalDateTime endDate);

    List<Object[]> getEmployeePerformanceReport(LocalDateTime startDate, LocalDateTime endDate);

    // ================================
    // INVENTORY & PARTS REPORTS
    // ================================

    List<Object[]> getPartUsageStatistics(LocalDateTime startDate, LocalDateTime endDate);

    List<Object[]> getPartsByVehicleBrand(LocalDateTime startDate, LocalDateTime endDate);

    List<Object[]> getMostUsedPartsByCategory(LocalDateTime startDate, LocalDateTime endDate);

    List<Object[]> getLowStockAlerts();

    // ================================
    // VEHICLE & BRAND REPORTS
    // ================================

    List<Object[]> getVehicleBrandStatistics(LocalDateTime startDate, LocalDateTime endDate);

    List<Object[]> getServiceTypeStatistics(LocalDateTime startDate, LocalDateTime endDate);

    // ================================
    // CLIENT REPORTS
    // ================================

    List<Object[]> getClientHistoryReport(LocalDateTime startDate, LocalDateTime endDate);

    List<Object[]> getClientServiceRatings(LocalDateTime startDate, LocalDateTime endDate);

    // ================================
    // MAINTENANCE REPORTS
    // ================================

    List<Object[]> getPreventiveMaintenanceReport(LocalDateTime startDate, LocalDateTime endDate);

    List<Object[]> getCorrectiveMaintenanceReport(LocalDateTime startDate, LocalDateTime endDate);

    // ================================
    // TOP PERFORMERS & ANALYTICS
    // ================================

    List<Object[]> getTopPerformingMechanics(LocalDateTime startDate, LocalDateTime endDate, int limit);

    List<ReportFileInfo> getAvailableReports();

    boolean deleteReport(String reportId);

    int cleanupExpiredReports();

}