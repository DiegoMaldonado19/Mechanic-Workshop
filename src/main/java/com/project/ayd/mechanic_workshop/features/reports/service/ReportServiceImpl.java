package com.project.ayd.mechanic_workshop.features.reports.service;

import com.project.ayd.mechanic_workshop.features.reports.dto.*;
import com.project.ayd.mechanic_workshop.features.reports.enums.ReportFormat;
import com.project.ayd.mechanic_workshop.features.reports.enums.ReportType;
import com.project.ayd.mechanic_workshop.features.reports.repository.ReportRepository;
import com.project.ayd.mechanic_workshop.features.reports.utils.ExcelGenerator;
import com.project.ayd.mechanic_workshop.features.reports.utils.PDFGenerator;
import com.project.ayd.mechanic_workshop.features.reports.utils.ReportGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final PDFGenerator pdfGenerator;
    private final ExcelGenerator excelGenerator;
    private final ReportGenerator reportGenerator;

    // Cache temporal para reportes generados
    private final Map<String, ReportResponse> reportCache = new ConcurrentHashMap<>();
    private static final String REPORTS_DIR = "temp/reports/";

    static {
        try {
            Files.createDirectories(Paths.get(REPORTS_DIR));
        } catch (IOException e) {
            log.error("Error creating reports directory", e);
        }
    }

    @Override
    @Transactional
    public ReportResponse generateReport(ReportRequest request) {
        log.info("Generating report of type: {} in format: {}", request.getReportType(), request.getFormat());

        // Determinar fechas
        LocalDateTime startDate = request.getStartDate();
        LocalDateTime endDate = request.getEndDate();

        if (request.getPeriod() != null) {
            startDate = request.getPeriod().getStartDate();
            endDate = request.getPeriod().getEndDate();
        }

        if (startDate == null)
            startDate = LocalDateTime.now().minusMonths(1);
        if (endDate == null)
            endDate = LocalDateTime.now();

        // Generar ID único para el reporte
        String reportId = generateReportId(request.getReportType(), request.getFormat());
        String fileName = generateFileName(request.getReportType(), request.getFormat(), startDate, endDate);
        String filePath = REPORTS_DIR + fileName;

        try {
            // Generar contenido del reporte según el tipo
            switch (request.getReportType()) {
                case FINANCIAL_INCOME:
                    generateFinancialIncomeReport(filePath, request.getFormat(), startDate, endDate);
                    break;
                case FINANCIAL_EXPENSES:
                    generateFinancialExpensesReport(filePath, request.getFormat(), startDate, endDate);
                    break;
                case WORK_BY_DATE:
                    generateWorksByDateReport(filePath, request.getFormat(), startDate, endDate);
                    break;
                case WORK_BY_TYPE:
                    generateWorksByTypeReport(filePath, request.getFormat(), startDate, endDate);
                    break;
                case WORK_BY_EMPLOYEE:
                    generateWorksByEmployeeReport(filePath, request.getFormat(), startDate, endDate);
                    break;
                case PARTS_USAGE:
                    generatePartsUsageReport(filePath, request.getFormat(), startDate, endDate);
                    break;
                case PARTS_BY_BRAND:
                    generatePartsByBrandReport(filePath, request.getFormat(), startDate, endDate);
                    break;
                case CLIENT_HISTORY:
                    generateClientHistoryReport(filePath, request.getFormat(), startDate, endDate);
                    break;
                case PREVENTIVE_MAINTENANCE:
                    generatePreventiveMaintenanceReport(filePath, request.getFormat(), startDate, endDate);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported report type: " + request.getReportType());
            }

            // Crear response del reporte
            ReportResponse response = ReportResponse.builder()
                    .reportId(reportId)
                    .reportType(request.getReportType())
                    .format(request.getFormat())
                    .fileName(fileName)
                    .downloadUrl("/api/v1/reports/download/" + reportId)
                    .generatedAt(LocalDateTime.now())
                    .generatedBy(getCurrentUsername())
                    .fileSize(getFileSize(filePath))
                    .expiresAt(LocalDateTime.now().plusHours(24))
                    .status("COMPLETED")
                    .build();

            // Guardar en cache
            reportCache.put(reportId, response);

            log.info("Report generated successfully: {}", reportId);
            return response;

        } catch (Exception e) {
            log.error("Error generating report", e);
            throw new RuntimeException("Failed to generate report: " + e.getMessage());
        }
    }

    @Override
    public Resource downloadReport(String reportId) {
        ReportResponse report = reportCache.get(reportId);
        if (report == null) {
            throw new IllegalArgumentException("Report not found or expired: " + reportId);
        }

        String filePath = REPORTS_DIR + report.getFileName();
        File file = new File(filePath);

        if (!file.exists()) {
            throw new IllegalArgumentException("Report file not found: " + reportId);
        }

        return new FileSystemResource(file);
    }

    @Override
    public List<ReportResponse> getReportHistory(String username) {
        return reportCache.values().stream()
                .filter(report -> username.equals(report.getGeneratedBy()))
                .filter(report -> report.getExpiresAt().isAfter(LocalDateTime.now()))
                .toList();
    }

    @Override
    public void deleteExpiredReports() {
        LocalDateTime now = LocalDateTime.now();
        List<String> expiredReportIds = reportCache.entrySet().stream()
                .filter(entry -> entry.getValue().getExpiresAt().isBefore(now))
                .map(Map.Entry::getKey)
                .toList();

        for (String reportId : expiredReportIds) {
            ReportResponse report = reportCache.remove(reportId);
            if (report != null) {
                try {
                    Files.deleteIfExists(Paths.get(REPORTS_DIR + report.getFileName()));
                } catch (IOException e) {
                    log.error("Error deleting expired report file: {}", report.getFileName(), e);
                }
            }
        }

        log.info("Deleted {} expired reports", expiredReportIds.size());
    }

    @Override
    @Transactional(readOnly = true)
    public FinancialReportResponse generateFinancialReport(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> incomeData = reportRepository.getIncomeByMonth(startDate.toLocalDate(), endDate.toLocalDate());

        BigDecimal totalIncome = incomeData.stream()
                .map(row -> new BigDecimal(row[1].toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Simulated expenses (en un sistema real, esto vendría de una tabla de gastos)
        BigDecimal totalExpenses = totalIncome.multiply(new BigDecimal("0.7")); // 70% de ingresos como gastos
        BigDecimal netProfit = totalIncome.subtract(totalExpenses);
        BigDecimal profitMargin = totalIncome.compareTo(BigDecimal.ZERO) > 0
                ? netProfit.divide(totalIncome, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"))
                : BigDecimal.ZERO;

        // Construir detalles de ingresos
        List<FinancialReportResponse.IncomeDetail> incomeBySource = List.of(
                FinancialReportResponse.IncomeDetail.builder()
                        .source("Servicios Mecánicos")
                        .amount(totalIncome)
                        .percentage(new BigDecimal("100"))
                        .transactionCount((long) incomeData.size())
                        .build());

        // Construir breakdown mensual
        List<FinancialReportResponse.MonthlyFinancial> monthlyBreakdown = new ArrayList<>();
        for (Object[] row : incomeData) {
            BigDecimal monthIncome = new BigDecimal(row[1].toString());
            BigDecimal monthExpenses = monthIncome.multiply(new BigDecimal("0.7"));

            monthlyBreakdown.add(FinancialReportResponse.MonthlyFinancial.builder()
                    .month((String) row[0])
                    .income(monthIncome)
                    .expenses(monthExpenses)
                    .profit(monthIncome.subtract(monthExpenses))
                    .build());
        }

        return FinancialReportResponse.builder()
                .reportPeriod(formatDateRange(startDate, endDate))
                .startDate(startDate.toLocalDate())
                .endDate(endDate.toLocalDate())
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netProfit(netProfit)
                .profitMargin(profitMargin)
                .incomeBySource(incomeBySource)
                .expensesByCategory(new ArrayList<>()) // Simplificado
                .monthlyBreakdown(monthlyBreakdown)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public OperationalReportResponse generateOperationalReport(LocalDateTime startDate, LocalDateTime endDate) {
        // Employee Performance
        List<Object[]> employeeData = reportRepository.getEmployeePerformance(startDate, endDate);
        List<OperationalReportResponse.EmployeePerformance> employeePerformances = new ArrayList<>();

        for (Object[] row : employeeData) {
            BigDecimal completionRate = BigDecimal.ZERO;
            if (((Number) row[2]).longValue() > 0) {
                completionRate = BigDecimal.valueOf(((Number) row[3]).longValue())
                        .divide(BigDecimal.valueOf(((Number) row[2]).longValue()), 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"));
            }

            employeePerformances.add(OperationalReportResponse.EmployeePerformance.builder()
                    .employeeName((String) row[0])
                    .employeeId(String.valueOf(row[1]))
                    .totalWorksAssigned(((Number) row[2]).longValue())
                    .completedWorks(((Number) row[3]).longValue())
                    .averageWorkTime(row[4] != null ? new BigDecimal(row[4].toString()) : BigDecimal.ZERO)
                    .completionRate(completionRate)
                    .totalRevenue(row[5] != null ? new BigDecimal(row[5].toString()) : BigDecimal.ZERO)
                    .build());
        }

        // Service Type Statistics
        List<Object[]> serviceTypeData = reportRepository.getServiceTypeStatistics(startDate, endDate);
        List<OperationalReportResponse.WorkTypeStatistics> workTypeStats = new ArrayList<>();

        for (Object[] row : serviceTypeData) {
            workTypeStats.add(OperationalReportResponse.WorkTypeStatistics.builder()
                    .serviceTypeName((String) row[0])
                    .totalWorks(((Number) row[1]).longValue())
                    .averageCost(row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO)
                    .averageDuration(row[3] != null ? new BigDecimal(row[3].toString()) : BigDecimal.ZERO)
                    .totalRevenue(row[4] != null ? new BigDecimal(row[4].toString()) : BigDecimal.ZERO)
                    .build());
        }

        // Vehicle Brand Statistics
        List<Object[]> vehicleBrandData = reportRepository.getVehicleBrandStatistics(startDate, endDate);
        List<OperationalReportResponse.VehicleBrandStatistics> vehicleBrandStats = new ArrayList<>();

        for (Object[] row : vehicleBrandData) {
            vehicleBrandStats.add(OperationalReportResponse.VehicleBrandStatistics.builder()
                    .brandName((String) row[0])
                    .totalWorks(((Number) row[1]).longValue())
                    .averageCost(row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO)
                    .uniqueVehicles(((Number) row[3]).longValue())
                    .build());
        }

        // Part Usage Statistics
        List<Object[]> partUsageData = reportRepository.getPartUsageStatistics(startDate, endDate);
        List<OperationalReportResponse.PartUsageStatistics> partUsageStats = new ArrayList<>();

        for (Object[] row : partUsageData) {
            partUsageStats.add(OperationalReportResponse.PartUsageStatistics.builder()
                    .partName((String) row[0])
                    .partCategory((String) row[1])
                    .totalQuantityUsed(((Number) row[2]).longValue())
                    .totalCost(row[3] != null ? new BigDecimal(row[3].toString()) : BigDecimal.ZERO)
                    .worksUsedIn(((Number) row[4]).longValue())
                    .build());
        }

        // Métricas generales
        Long totalWorks = employeePerformances.stream()
                .mapToLong(OperationalReportResponse.EmployeePerformance::getTotalWorksAssigned)
                .sum();
        Long completedWorks = employeePerformances.stream()
                .mapToLong(OperationalReportResponse.EmployeePerformance::getCompletedWorks)
                .sum();

        return OperationalReportResponse.builder()
                .reportPeriod(formatDateRange(startDate, endDate))
                .startDate(startDate.toLocalDate())
                .endDate(endDate.toLocalDate())
                .totalWorks(totalWorks)
                .completedWorks(completedWorks)
                .pendingWorks(totalWorks - completedWorks)
                .cancelledWorks(0L) // Simplificado
                .averageWorkDuration(BigDecimal.ZERO) // Calcular si es necesario
                .workCompletionRate(totalWorks > 0
                        ? BigDecimal.valueOf(completedWorks)
                                .divide(BigDecimal.valueOf(totalWorks), 4, RoundingMode.HALF_UP)
                                .multiply(new BigDecimal("100"))
                        : BigDecimal.ZERO)
                .employeePerformances(employeePerformances)
                .workTypeStats(workTypeStats)
                .vehicleBrandStats(vehicleBrandStats)
                .partUsageStats(partUsageStats)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getIncomeByWeek(LocalDateTime startDate, LocalDateTime endDate) {
        return reportRepository.getIncomeByMonth(startDate.toLocalDate(), endDate.toLocalDate()); // Simplificado
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getPreventiveMaintenanceReport(LocalDateTime startDate, LocalDateTime endDate) {
        return reportRepository.getPreventiveMaintenanceReport(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getPartsByVehicleBrand(LocalDateTime startDate, LocalDateTime endDate) {
        return reportRepository.getPartsByVehicleBrand(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getClientHistoryReport(LocalDateTime startDate, LocalDateTime endDate) {
        return reportRepository.getClientHistory(startDate, endDate);
    }

    @Override
    public byte[] exportReportData(ReportType reportType, LocalDateTime startDate,
            LocalDateTime endDate, String format) {
        try {
            switch (format.toUpperCase()) {
                case "PDF":
                    return pdfGenerator.generateReport(reportType, startDate, endDate);
                case "EXCEL":
                    return excelGenerator.generateReport(reportType, startDate, endDate);
                default:
                    throw new IllegalArgumentException("Unsupported format: " + format);
            }
        } catch (Exception e) {
            log.error("Error exporting report data", e);
            throw new RuntimeException("Failed to export report: " + e.getMessage());
        }
    }

    // Helper methods
    private void generateFinancialIncomeReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        try {
            reportGenerator.generateFinancialIncomeReport(filePath, format, startDate, endDate);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate financial income report", e);
        }
    }

    private void generateFinancialExpensesReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        try {
            reportGenerator.generateFinancialExpensesReport(filePath, format, startDate, endDate);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate financial expenses report", e);
        }
    }

    private void generateWorksByDateReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        try {
            reportGenerator.generateWorksByDateReport(filePath, format, startDate, endDate);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate works by date report", e);
        }
    }

    private void generateWorksByTypeReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        try {
            reportGenerator.generateWorksByTypeReport(filePath, format, startDate, endDate);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate works by type report", e);
        }
    }

    private void generateWorksByEmployeeReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        try {
            reportGenerator.generateWorksByEmployeeReport(filePath, format, startDate, endDate);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate works by employee report", e);
        }
    }

    private void generatePartsUsageReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        try {
            reportGenerator.generatePartsUsageReport(filePath, format, startDate, endDate);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate parts usage report", e);
        }
    }

    private void generatePartsByBrandReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        try {
            reportGenerator.generatePartsByBrandReport(filePath, format, startDate, endDate);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate parts by brand report", e);
        }
    }

    private void generateClientHistoryReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        try {
            reportGenerator.generateClientHistoryReport(filePath, format, startDate, endDate);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate client history report", e);
        }
    }

    private void generatePreventiveMaintenanceReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        try {
            reportGenerator.generatePreventiveMaintenanceReport(filePath, format, startDate, endDate);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate preventive maintenance report", e);
        }
    }

    private String generateReportId(ReportType type, ReportFormat format) {
        return String.format("%s_%s_%d",
                type.name(), format.name(), System.currentTimeMillis());
    }

    private String generateFileName(ReportType type, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return String.format("%s_%s_%s%s",
                type.name().toLowerCase(),
                startDate.format(formatter) + "_to_" + endDate.format(formatter),
                System.currentTimeMillis(),
                format.getFileExtension());
    }

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private Long getFileSize(String filePath) {
        try {
            return Files.size(Paths.get(filePath));
        } catch (IOException e) {
            log.warn("Could not get file size for: {}", filePath);
            return 0L;
        }
    }

    private String formatDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return startDate.format(formatter) + " - " + endDate.format(formatter);
    }
}