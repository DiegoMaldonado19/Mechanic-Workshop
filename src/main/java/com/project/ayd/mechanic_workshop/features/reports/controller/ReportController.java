package com.project.ayd.mechanic_workshop.features.reports.controller;

import com.project.ayd.mechanic_workshop.features.reports.dto.*;
import com.project.ayd.mechanic_workshop.features.reports.enums.ReportFormat;
import com.project.ayd.mechanic_workshop.features.reports.enums.ReportPeriod;
import com.project.ayd.mechanic_workshop.features.reports.enums.ReportType;
import com.project.ayd.mechanic_workshop.features.reports.service.DashboardService;
import com.project.ayd.mechanic_workshop.features.reports.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO')")
public class ReportController {

    private final ReportService reportService;
    private final DashboardService dashboardService;

    // Dashboard endpoints
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO', 'ESPECIALISTA')")
    public ResponseEntity<DashboardResponse> getDashboard() {
        DashboardResponse dashboard = dashboardService.getDashboardData();
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/dashboard/period")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<DashboardResponse> getDashboardForPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        DashboardResponse dashboard = dashboardService.getDashboardDataForPeriod(startDate, endDate);
        return ResponseEntity.ok(dashboard);
    }

    @PostMapping("/dashboard/refresh")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, String>> refreshDashboard() {
        dashboardService.refreshDashboardCache();
        return ResponseEntity.ok(Map.of("message", "Dashboard cache refreshed successfully"));
    }

    // Report generation endpoints
    @PostMapping("/generate")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ReportResponse> generateReport(@Valid @RequestBody ReportRequest request) {
        ReportResponse report = reportService.generateReport(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(report);
    }

    @GetMapping("/download/{reportId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO')")
    public ResponseEntity<Resource> downloadReport(@PathVariable String reportId) {
        try {
            Resource resource = reportService.downloadReport(reportId);

            // Determinar el content type basado en la extensión del archivo
            String contentType = "application/octet-stream";
            String filename = resource.getFilename();

            if (filename != null) {
                if (filename.endsWith(".pdf")) {
                    contentType = "application/pdf";
                } else if (filename.endsWith(".xlsx")) {
                    contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                } else if (filename.endsWith(".csv")) {
                    contentType = "text/csv";
                } else if (filename.endsWith(".png")) {
                    contentType = "image/png";
                }
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO')")
    public ResponseEntity<List<ReportResponse>> getReportHistory(Authentication authentication) {
        String username = authentication.getName();
        List<ReportResponse> history = reportService.getReportHistory(username);
        return ResponseEntity.ok(history);
    }

    // Financial reports
    @GetMapping("/financial")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<FinancialReportResponse> getFinancialReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        if (startDate == null)
            startDate = LocalDateTime.now().minusMonths(1);
        if (endDate == null)
            endDate = LocalDateTime.now();

        FinancialReportResponse report = reportService.generateFinancialReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/operational")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<OperationalReportResponse> getOperationalReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        if (startDate == null)
            startDate = LocalDateTime.now().minusMonths(1);
        if (endDate == null)
            endDate = LocalDateTime.now();

        OperationalReportResponse report = reportService.generateOperationalReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    // Specific report data endpoints
    @GetMapping("/income/weekly")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<Object[]>> getIncomeByWeek(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Object[]> data = reportService.getIncomeByWeek(startDate, endDate);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/maintenance/preventive")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO')")
    public ResponseEntity<List<Object[]>> getPreventiveMaintenanceReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Object[]> data = reportService.getPreventiveMaintenanceReport(startDate, endDate);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/parts/by-brand")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO')")
    public ResponseEntity<List<Object[]>> getPartsByVehicleBrand(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Object[]> data = reportService.getPartsByVehicleBrand(startDate, endDate);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/clients/history")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<Object[]>> getClientHistoryReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Object[]> data = reportService.getClientHistoryReport(startDate, endDate);
        return ResponseEntity.ok(data);
    }

    // Export endpoints
    @PostMapping("/export")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<byte[]> exportReport(
            @RequestParam ReportType reportType,
            @RequestParam ReportFormat format,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        byte[] reportData = reportService.exportReportData(reportType, startDate, endDate, format.name());

        String filename = generateExportFilename(reportType, format);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(format.getMimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(reportData);
    }

    // Configuration endpoints
    @GetMapping("/types")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO')")
    public ResponseEntity<List<Map<String, String>>> getReportTypes() {
        List<Map<String, String>> types = Arrays.stream(ReportType.values())
                .map(type -> Map.of(
                        "value", type.name(),
                        "label", type.getDisplayName()))
                .toList();
        return ResponseEntity.ok(types);
    }

    @GetMapping("/formats")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO')")
    public ResponseEntity<List<Map<String, String>>> getReportFormats() {
        List<Map<String, String>> formats = Arrays.stream(ReportFormat.values())
                .map(format -> Map.of(
                        "value", format.name(),
                        "label", format.getDisplayName(),
                        "mimeType", format.getMimeType(),
                        "extension", format.getFileExtension()))
                .toList();
        return ResponseEntity.ok(formats);
    }

    @GetMapping("/periods")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO')")
    public ResponseEntity<List<Map<String, String>>> getReportPeriods() {
        List<Map<String, String>> periods = Arrays.stream(ReportPeriod.values())
                .map(period -> Map.of(
                        "value", period.name(),
                        "label", period.getDisplayName()))
                .toList();
        return ResponseEntity.ok(periods);
    }

    // Chart data endpoints for frontend
    @GetMapping("/charts/income")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO')")
    public ResponseEntity<DashboardResponse.ChartData[]> getIncomeChartData(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        if (startDate == null)
            startDate = LocalDateTime.now().minusMonths(6);
        if (endDate == null)
            endDate = LocalDateTime.now();

        DashboardResponse.ChartData[] data = dashboardService.getIncomeChartData(startDate, endDate);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/charts/work-status")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO')")
    public ResponseEntity<DashboardResponse.ChartData[]> getWorkStatusChartData() {
        DashboardResponse.ChartData[] data = dashboardService.getWorkStatusChartData();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/charts/work-types")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO')")
    public ResponseEntity<DashboardResponse.ChartData[]> getWorkTypeChartData(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        if (startDate == null)
            startDate = LocalDateTime.now().minusMonths(3);
        if (endDate == null)
            endDate = LocalDateTime.now();

        DashboardResponse.ChartData[] data = dashboardService.getWorkTypeChartData(startDate, endDate);
        return ResponseEntity.ok(data);
    }

    // Admin maintenance endpoints
    @DeleteMapping("/cleanup")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, String>> cleanupExpiredReports() {
        reportService.deleteExpiredReports();
        return ResponseEntity.ok(Map.of("message", "Expired reports cleaned up successfully"));
    }

    // Helper methods
    private String generateExportFilename(ReportType reportType, ReportFormat format) {
        return String.format("%s_%s%s",
                reportType.name().toLowerCase(),
                System.currentTimeMillis(),
                format.getFileExtension());
    }

    // ================================
    // FINANCIAL REPORT ENDPOINTS
    // ================================

    @GetMapping("/financial/income/monthly")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<Object[]>> getIncomeByMonth(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Object[]> data = reportService.getIncomeByMonth(startDate, endDate);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/financial/expenses/monthly")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<Object[]>> getExpensesByMonth(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Object[]> data = reportService.getExpensesByMonth(startDate, endDate);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/financial/providers")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<Object[]>> getProviderExpenses(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Object[]> data = reportService.getProviderExpenses(startDate, endDate);
        return ResponseEntity.ok(data);
    }

    // ================================
    // OPERATIONAL REPORT ENDPOINTS
    // ================================

    @GetMapping("/operational/works/by-date-type")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO')")
    public ResponseEntity<List<Object[]>> getWorksByDateAndType(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Object[]> data = reportService.getWorksByDateAndType(startDate, endDate);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/operational/works/by-employee")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO')")
    public ResponseEntity<List<Object[]>> getWorksByEmployee(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Object[]> data = reportService.getWorksByEmployee(startDate, endDate);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/operational/vehicle/history")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO')")
    public ResponseEntity<List<Object[]>> getVehicleMaintenanceHistory(
            @RequestParam String licensePlate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        if (startDate == null)
            startDate = LocalDateTime.now().minusYears(2);
        if (endDate == null)
            endDate = LocalDateTime.now();

        List<Object[]> data = reportService.getVehicleMaintenanceHistory(licensePlate, startDate, endDate);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/operational/employee/performance")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<Object[]>> getEmployeePerformanceReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Object[]> data = reportService.getEmployeePerformanceReport(startDate, endDate);
        return ResponseEntity.ok(data);
    }

    // ================================
    // INVENTORY & PARTS ENDPOINTS
    // ================================

    @GetMapping("/inventory/parts/usage")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO')")
    public ResponseEntity<List<Object[]>> getPartUsageStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Object[]> data = reportService.getPartUsageStatistics(startDate, endDate);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/inventory/parts/by-category")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO')")
    public ResponseEntity<List<Object[]>> getMostUsedPartsByCategory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Object[]> data = reportService.getMostUsedPartsByCategory(startDate, endDate);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/inventory/alerts/low-stock")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO')")
    public ResponseEntity<List<Object[]>> getLowStockAlerts() {
        List<Object[]> data = reportService.getLowStockAlerts();
        return ResponseEntity.ok(data);
    }

    // ================================
    // VEHICLE & BRAND ENDPOINTS
    // ================================

    @GetMapping("/vehicles/brand-statistics")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO')")
    public ResponseEntity<List<Object[]>> getVehicleBrandStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Object[]> data = reportService.getVehicleBrandStatistics(startDate, endDate);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/services/type-statistics")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO')")
    public ResponseEntity<List<Object[]>> getServiceTypeStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Object[]> data = reportService.getServiceTypeStatistics(startDate, endDate);
        return ResponseEntity.ok(data);
    }

    // ================================
    // CLIENT REPORT ENDPOINTS
    // ================================

    @GetMapping("/clients/service-ratings")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<Object[]>> getClientServiceRatings(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Object[]> data = reportService.getClientServiceRatings(startDate, endDate);
        return ResponseEntity.ok(data);
    }

    // ================================
    // MAINTENANCE REPORT ENDPOINTS
    // ================================

    @GetMapping("/maintenance/corrective")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO')")
    public ResponseEntity<List<Object[]>> getCorrectiveMaintenanceReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Object[]> data = reportService.getCorrectiveMaintenanceReport(startDate, endDate);
        return ResponseEntity.ok(data);
    }

    // ================================
    // TOP PERFORMERS ENDPOINTS
    // ================================

    @GetMapping("/analytics/top-mechanics")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<Object[]>> getTopPerformingMechanics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "10") int limit) {
        List<Object[]> data = reportService.getTopPerformingMechanics(startDate, endDate, limit);
        return ResponseEntity.ok(data);
    }

    // ================================
    // COMPREHENSIVE EXPORT ENDPOINTS
    // ================================

    @PostMapping("/export/comprehensive")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, String>> generateComprehensiveReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam ReportFormat format) {

        try {
            // Generar múltiples reportes y combinarlos
            String reportId = reportService.generateReport(ReportRequest.builder()
                    .reportType(ReportType.DASHBOARD_SUMMARY)
                    .format(format)
                    .startDate(startDate)
                    .endDate(endDate)
                    .build()).getReportId();

            return ResponseEntity.ok(Map.of(
                    "message", "Comprehensive report generated successfully",
                    "reportId", reportId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to generate comprehensive report"));
        }
    }

    // ================================
    // BULK EXPORT FUNCTIONALITY
    // ================================

    @PostMapping("/export/bulk")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Object>> generateBulkReports(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam List<ReportType> reportTypes,
            @RequestParam ReportFormat format) {

        try {
            List<String> reportIds = new ArrayList<>();

            for (ReportType reportType : reportTypes) {
                String reportId = reportService.generateReport(ReportRequest.builder()
                        .reportType(reportType)
                        .format(format)
                        .startDate(startDate)
                        .endDate(endDate)
                        .build()).getReportId();
                reportIds.add(reportId);
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Bulk reports generated successfully",
                    "reportIds", reportIds,
                    "count", reportIds.size()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to generate bulk reports"));
        }
    }
}