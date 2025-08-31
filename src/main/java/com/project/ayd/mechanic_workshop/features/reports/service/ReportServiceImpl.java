package com.project.ayd.mechanic_workshop.features.reports.service;

import com.project.ayd.mechanic_workshop.features.reports.dto.*;
import com.project.ayd.mechanic_workshop.features.reports.enums.ReportFormat;
import com.project.ayd.mechanic_workshop.features.reports.enums.ReportType;
import com.project.ayd.mechanic_workshop.features.reports.repository.ReportRepository;
import com.project.ayd.mechanic_workshop.features.reports.utils.ExcelGenerator;
import com.project.ayd.mechanic_workshop.features.reports.utils.PDFGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
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

    // Cache temporal para reportes generados - incluye path del archivo
    private final Map<String, String> reportPathCache = new ConcurrentHashMap<>();
    private final Map<String, ReportResponse> reportCache = new ConcurrentHashMap<>();
    private static final String REPORTS_DIR = "temp/reports/";

    static {
        try {
            Files.createDirectories(Paths.get(REPORTS_DIR));
        } catch (IOException e) {
            log.error("Error creating reports directory", e);
        }
    }

    // ================================
    // MÉTODOS PRINCIPALES DE LA INTERFAZ
    // ================================

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
        String reportType = request.getReportType().toString();

        try {
            // Generar contenido del reporte según el tipo
            switch (reportType) {
                case "FINANCIAL_INCOME":
                    generateFinancialIncomeReport(filePath, request.getFormat(), startDate, endDate);
                    break;
                case "FINANCIAL_EXPENSES":
                    generateFinancialExpensesReport(filePath, request.getFormat(), startDate, endDate);
                    break;
                case "WORK_BY_DATE":
                    generateWorksByDateReport(filePath, request.getFormat(), startDate, endDate);
                    break;
                case "WORK_BY_TYPE":
                    generateWorksByTypeReport(filePath, request.getFormat(), startDate, endDate);
                    break;
                case "WORK_BY_EMPLOYEE":
                    generateWorksByEmployeeReport(filePath, request.getFormat(), startDate, endDate);
                    break;
                case "PARTS_USAGE":
                    generatePartsUsageReport(filePath, request.getFormat(), startDate, endDate);
                    break;
                case "PARTS_BY_BRAND":
                    generatePartsByBrandReport(filePath, request.getFormat(), startDate, endDate);
                    break;
                case "CLIENT_HISTORY":
                    generateClientHistoryReport(filePath, request.getFormat(), startDate, endDate);
                    break;
                case "PREVENTIVE_MAINTENANCE":
                    generatePreventiveMaintenanceReport(filePath, request.getFormat(), startDate, endDate);
                    break;
                case "CORRECTIVE_MAINTENANCE":
                    generateCorrectiveMaintenanceReport(filePath, request.getFormat(), startDate, endDate);
                    break;
                case "PAYMENT_STATUS":
                    generatePaymentStatusReport(filePath, request.getFormat(), startDate, endDate);
                    break;
                case "INVENTORY_STOCK":
                    generateInventoryStockReport(filePath, request.getFormat(), startDate, endDate);
                    break;
                case "VEHICLE_BEHAVIOR":
                    generateVehicleBehaviorReport(filePath, request.getFormat(), startDate, endDate);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported report type: " + request.getReportType());
            }

            // Crear response del reporte
            String currentUser = getCurrentUsername();
            Long fileSize = getFileSize(filePath);

            ReportResponse response = ReportResponse.builder()
                    .reportId(reportId)
                    .reportType(request.getReportType())
                    .format(request.getFormat())
                    .fileName(fileName)
                    .downloadUrl("/api/reports/download/" + reportId)
                    .generatedAt(LocalDateTime.now())
                    .generatedBy(currentUser)
                    .fileSize(fileSize)
                    .expiresAt(LocalDateTime.now().plusDays(7))
                    .status("COMPLETED")
                    .build();

            // Guardar en cache temporal
            reportCache.put(reportId, response);
            reportPathCache.put(reportId, filePath);

            log.info("Report generated successfully: {}", reportId);
            return response;

        } catch (Exception e) {
            log.error("Error generating report: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate report: " + e.getMessage());
        }
    }

    @Override
    public Resource downloadReport(String reportId) {
        log.info("Downloading report: {}", reportId);

        ReportResponse report = reportCache.get(reportId);
        String filePath = reportPathCache.get(reportId);

        if (report == null || filePath == null) {
            throw new RuntimeException("Report not found or expired: " + reportId);
        }

        File file = new File(filePath);
        if (!file.exists()) {
            throw new RuntimeException("Report file not found: " + reportId);
        }

        return new FileSystemResource(file);
    }

    @Override
    public List<ReportResponse> getReportHistory(String username) {
        log.info("Getting report history for user: {}", username);
        return reportCache.values().stream()
                .filter(report -> username.equals(report.getGeneratedBy()))
                .filter(report -> report.getExpiresAt().isAfter(LocalDateTime.now()))
                .sorted((r1, r2) -> r2.getGeneratedAt().compareTo(r1.getGeneratedAt()))
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
            String filePath = reportPathCache.remove(reportId);

            if (report != null && filePath != null) {
                try {
                    Files.deleteIfExists(Paths.get(filePath));
                } catch (IOException e) {
                    log.error("Error deleting expired report file: {}", report.getFileName(), e);
                }
            }
        }

        log.info("Deleted {} expired reports", expiredReportIds.size());
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

    // ================================
    // MÉTODOS DE REPORTES CONSOLIDADOS
    // ================================

    @Override
    @Transactional(readOnly = true)
    public FinancialReportResponse generateFinancialReport(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> incomeData = reportRepository.getIncomeByMonth(startDate.toLocalDate(), endDate.toLocalDate());

        BigDecimal totalIncome = incomeData.stream()
                .map(row -> new BigDecimal(row[1].toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = totalIncome.multiply(new BigDecimal("0.7"));
        BigDecimal netProfit = totalIncome.subtract(totalExpenses);
        BigDecimal profitMargin = totalIncome.compareTo(BigDecimal.ZERO) > 0
                ? netProfit.divide(totalIncome, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"))
                : BigDecimal.ZERO;

        List<FinancialReportResponse.IncomeDetail> incomeBySource = List.of(
                FinancialReportResponse.IncomeDetail.builder()
                        .source("Servicios Mecánicos")
                        .amount(totalIncome)
                        .percentage(new BigDecimal("100"))
                        .transactionCount((long) incomeData.size())
                        .build());

        return FinancialReportResponse.builder()
                .startDate(startDate.toLocalDate()) // Convertir a LocalDate
                .endDate(endDate.toLocalDate()) // Convertir a LocalDate
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netProfit(netProfit)
                .profitMargin(profitMargin)
                .incomeBySource(incomeBySource)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public OperationalReportResponse generateOperationalReport(LocalDateTime startDate, LocalDateTime endDate) {
        return OperationalReportResponse.builder()
                .startDate(startDate.toLocalDate()) // Convertir a LocalDate
                .endDate(endDate.toLocalDate()) // Convertir a LocalDate
                .totalWorks(reportRepository.countActiveWorks() + reportRepository.countCompletedWorks())
                .completedWorks(reportRepository.countCompletedWorks())
                .pendingWorks(reportRepository.countPendingWorks())
                .build();
    }

    // ================================
    // IMPLEMENTACIÓN DE MÉTODOS DE LA INTERFAZ
    // ================================

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getIncomeByWeek(LocalDateTime startDate, LocalDateTime endDate) {
        return reportRepository.getIncomeByWeek(startDate.toLocalDate(), endDate.toLocalDate());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getIncomeByMonth(LocalDateTime startDate, LocalDateTime endDate) {
        return reportRepository.getIncomeByMonth(startDate.toLocalDate(), endDate.toLocalDate());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getExpensesByMonth(LocalDateTime startDate, LocalDateTime endDate) {
        return reportRepository.getExpensesByMonth(startDate.toLocalDate(), endDate.toLocalDate());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getProviderExpenses(LocalDateTime startDate, LocalDateTime endDate) {
        return reportRepository.getProviderExpenses(startDate.toLocalDate(), endDate.toLocalDate());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getWorksByDateAndType(LocalDateTime startDate, LocalDateTime endDate) {
        return reportRepository.getWorksByDateAndType(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getWorksByEmployee(LocalDateTime startDate, LocalDateTime endDate) {
        return reportRepository.getWorksByEmployee(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getVehicleMaintenanceHistory(String licensePlate, LocalDateTime startDate,
            LocalDateTime endDate) {
        return reportRepository.getVehicleMaintenanceHistory(licensePlate, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getEmployeePerformanceReport(LocalDateTime startDate, LocalDateTime endDate) {
        return reportRepository.getEmployeePerformance(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getPartUsageStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        return reportRepository.getPartUsageStatistics(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getPartsByVehicleBrand(LocalDateTime startDate, LocalDateTime endDate) {
        return reportRepository.getPartsByVehicleBrand(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getMostUsedPartsByCategory(LocalDateTime startDate, LocalDateTime endDate) {
        return reportRepository.getMostUsedPartsByCategory(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getLowStockAlerts() {
        return reportRepository.getLowStockAlerts();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getVehicleBrandStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        return reportRepository.getVehicleBrandStatistics(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getServiceTypeStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        return reportRepository.getServiceTypeStatistics(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getClientHistoryReport(LocalDateTime startDate, LocalDateTime endDate) {
        return reportRepository.getClientHistory(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getClientServiceRatings(LocalDateTime startDate, LocalDateTime endDate) {
        return reportRepository.getClientServiceRatings(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getPreventiveMaintenanceReport(LocalDateTime startDate, LocalDateTime endDate) {
        return reportRepository.getPreventiveMaintenanceReport(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getCorrectiveMaintenanceReport(LocalDateTime startDate, LocalDateTime endDate) {
        return reportRepository.getCorrectiveMaintenanceReport(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getTopPerformingMechanics(LocalDateTime startDate, LocalDateTime endDate, int limit) {
        return reportRepository.getTopPerformingMechanics(startDate, endDate, limit);
    }

    // ================================
    // MÉTODOS HELPER PARA GENERACIÓN DE REPORTES
    // ================================

    private void generateFinancialIncomeReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating financial income report: {} - {}", startDate, endDate);

        try {
            List<Object[]> reportData = reportRepository.getIncomeByMonth(startDate.toLocalDate(),
                    endDate.toLocalDate());

            switch (format) {
                case PDF:
                    generateGenericPDFReport(filePath, reportData,
                            "REPORTE FINANCIERO - INGRESOS",
                            new String[] { "Mes", "Ingresos" },
                            startDate, endDate);
                    break;
                case EXCEL:
                    generateGenericExcelReport(filePath, reportData,
                            "REPORTE FINANCIERO - INGRESOS",
                            new String[] { "Mes", "Ingresos" },
                            startDate, endDate);
                    break;
                case CSV:
                    generateCSVReport(filePath, reportData, new String[] { "Mes", "Ingresos" });
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported format: " + format);
            }
        } catch (IOException e) {
            log.error("Error generating financial income report", e);
            throw new RuntimeException("Failed to generate financial income report", e);
        }
    }

    private void generateFinancialExpensesReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating financial expenses report: {} - {}", startDate, endDate);

        try {
            List<Object[]> reportData = reportRepository.getExpensesByMonth(startDate.toLocalDate(),
                    endDate.toLocalDate());

            switch (format) {
                case PDF:
                    generateGenericPDFReport(filePath, reportData,
                            "REPORTE FINANCIERO - GASTOS",
                            new String[] { "Mes", "Gastos" },
                            startDate, endDate);
                    break;
                case EXCEL:
                    generateGenericExcelReport(filePath, reportData,
                            "REPORTE FINANCIERO - GASTOS",
                            new String[] { "Mes", "Gastos" },
                            startDate, endDate);
                    break;
                case CSV:
                    generateCSVReport(filePath, reportData, new String[] { "Mes", "Gastos" });
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported format: " + format);
            }
        } catch (IOException e) {
            log.error("Error generating financial expenses report", e);
            throw new RuntimeException("Failed to generate financial expenses report", e);
        }
    }

    private void generateWorksByDateReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating works by date report: {} - {}", startDate, endDate);

        try {
            List<Object[]> reportData = reportRepository.getWorksByStatus(startDate, endDate);

            switch (format) {
                case PDF:
                    generateGenericPDFReport(filePath, reportData,
                            "REPORTE DE TRABAJOS POR FECHA",
                            new String[] { "Estado", "Cantidad" },
                            startDate, endDate);
                    break;
                case EXCEL:
                    generateGenericExcelReport(filePath, reportData,
                            "REPORTE DE TRABAJOS POR FECHA",
                            new String[] { "Estado", "Cantidad" },
                            startDate, endDate);
                    break;
                case CSV:
                    generateCSVReport(filePath, reportData, new String[] { "Estado", "Cantidad" });
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported format: " + format);
            }
        } catch (IOException e) {
            log.error("Error generating works by date report", e);
            throw new RuntimeException("Failed to generate works by date report", e);
        }
    }

    private void generateWorksByTypeReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating works by type report: {} - {}", startDate, endDate);

        try {
            List<Object[]> reportData = reportRepository.getWorksByType(startDate, endDate);

            switch (format) {
                case PDF:
                    generateGenericPDFReport(filePath, reportData,
                            "REPORTE DE TRABAJOS POR TIPO",
                            new String[] { "Tipo de Servicio", "Cantidad" },
                            startDate, endDate);
                    break;
                case EXCEL:
                    generateGenericExcelReport(filePath, reportData,
                            "REPORTE DE TRABAJOS POR TIPO",
                            new String[] { "Tipo de Servicio", "Cantidad" },
                            startDate, endDate);
                    break;
                case CSV:
                    generateCSVReport(filePath, reportData, new String[] { "Tipo de Servicio", "Cantidad" });
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported format: " + format);
            }
        } catch (IOException e) {
            log.error("Error generating works by type report", e);
            throw new RuntimeException("Failed to generate works by type report", e);
        }
    }

    private void generateWorksByEmployeeReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating works by employee report: {} - {}", startDate, endDate);

        try {
            List<Object[]> reportData = reportRepository.getEmployeePerformance(startDate, endDate);

            switch (format) {
                case PDF:
                    generateGenericPDFReport(filePath, reportData,
                            "REPORTE DE TRABAJOS POR EMPLEADO",
                            new String[] { "Empleado", "ID", "Trabajos Totales", "Trabajos Completados",
                                    "Tiempo Promedio", "Ingresos Totales", "Tasa Completado" },
                            startDate, endDate);
                    break;
                case EXCEL:
                    generateGenericExcelReport(filePath, reportData,
                            "REPORTE DE TRABAJOS POR EMPLEADO",
                            new String[] { "Empleado", "ID", "Trabajos Totales", "Trabajos Completados",
                                    "Tiempo Promedio", "Ingresos Totales", "Tasa Completado" },
                            startDate, endDate);
                    break;
                case CSV:
                    generateCSVReport(filePath, reportData, new String[] { "Empleado", "ID", "Trabajos Totales",
                            "Trabajos Completados", "Tiempo Promedio", "Ingresos Totales", "Tasa Completado" });
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported format: " + format);
            }
        } catch (IOException e) {
            log.error("Error generating works by employee report", e);
            throw new RuntimeException("Failed to generate works by employee report", e);
        }
    }

    private void generatePartsUsageReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating parts usage report: {} - {}", startDate, endDate);

        try {
            List<Object[]> reportData = reportRepository.getPartUsageStatistics(startDate, endDate);

            switch (format) {
                case PDF:
                    generateGenericPDFReport(filePath, reportData,
                            "REPORTE DE USO DE REPUESTOS",
                            new String[] { "Repuesto", "Categoría", "Cantidad Total", "Costo Total", "Trabajos",
                                    "Precio Promedio" },
                            startDate, endDate);
                    break;
                case EXCEL:
                    generateGenericExcelReport(filePath, reportData,
                            "REPORTE DE USO DE REPUESTOS",
                            new String[] { "Repuesto", "Categoría", "Cantidad Total", "Costo Total", "Trabajos",
                                    "Precio Promedio" },
                            startDate, endDate);
                    break;
                case CSV:
                    generateCSVReport(filePath, reportData, new String[] { "Repuesto", "Categoría", "Cantidad Total",
                            "Costo Total", "Trabajos", "Precio Promedio" });
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported format: " + format);
            }
        } catch (IOException e) {
            log.error("Error generating parts usage report", e);
            throw new RuntimeException("Failed to generate parts usage report", e);
        }
    }

    private void generatePartsByBrandReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating parts by brand report: {} - {}", startDate, endDate);

        try {
            List<Object[]> reportData = reportRepository.getPartsByVehicleBrand(startDate, endDate);

            switch (format) {
                case PDF:
                    generateGenericPDFReport(filePath, reportData,
                            "REPORTE DE REPUESTOS POR MARCA DE VEHÍCULO",
                            new String[] { "Marca", "Repuesto", "Categoría", "Cantidad Total", "Costo Total" },
                            startDate, endDate);
                    break;
                case EXCEL:
                    generateGenericExcelReport(filePath, reportData,
                            "REPORTE DE REPUESTOS POR MARCA DE VEHÍCULO",
                            new String[] { "Marca", "Repuesto", "Categoría", "Cantidad Total", "Costo Total" },
                            startDate, endDate);
                    break;
                case CSV:
                    generateCSVReport(filePath, reportData,
                            new String[] { "Marca", "Repuesto", "Categoría", "Cantidad Total", "Costo Total" });
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported format: " + format);
            }
        } catch (IOException e) {
            log.error("Error generating parts by brand report", e);
            throw new RuntimeException("Failed to generate parts by brand report", e);
        }
    }

    private void generateClientHistoryReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating client history report: {} - {}", startDate, endDate);

        try {
            List<Object[]> reportData = reportRepository.getClientHistory(startDate, endDate);

            switch (format) {
                case PDF:
                    generateGenericPDFReport(filePath, reportData,
                            "REPORTE DE HISTORIAL DE CLIENTES",
                            new String[] { "Cliente", "CUI", "Email", "Trabajos Totales", "Total Gastado",
                                    "Última Visita", "Vehículos", "Tipos de Servicio" },
                            startDate, endDate);
                    break;
                case EXCEL:
                    generateGenericExcelReport(filePath, reportData,
                            "REPORTE DE HISTORIAL DE CLIENTES",
                            new String[] { "Cliente", "CUI", "Email", "Trabajos Totales", "Total Gastado",
                                    "Última Visita", "Vehículos", "Tipos de Servicio" },
                            startDate, endDate);
                    break;
                case CSV:
                    generateCSVReport(filePath, reportData, new String[] { "Cliente", "CUI", "Email",
                            "Trabajos Totales", "Total Gastado", "Última Visita", "Vehículos", "Tipos de Servicio" });
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported format: " + format);
            }
        } catch (IOException e) {
            log.error("Error generating client history report", e);
            throw new RuntimeException("Failed to generate client history report", e);
        }
    }

    private void generatePreventiveMaintenanceReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating preventive maintenance report: {} - {}", startDate, endDate);

        try {
            List<Object[]> reportData = reportRepository.getPreventiveMaintenanceReport(startDate, endDate);

            switch (format) {
                case PDF:
                    generateGenericPDFReport(filePath, reportData,
                            "REPORTE DE MANTENIMIENTO PREVENTIVO",
                            new String[] { "Fecha", "Trabajos Totales", "Costo Promedio", "Duración Promedio",
                                    "Ingresos Totales", "Trabajos Completados" },
                            startDate, endDate);
                    break;
                case EXCEL:
                    generateGenericExcelReport(filePath, reportData,
                            "REPORTE DE MANTENIMIENTO PREVENTIVO",
                            new String[] { "Fecha", "Trabajos Totales", "Costo Promedio", "Duración Promedio",
                                    "Ingresos Totales", "Trabajos Completados" },
                            startDate, endDate);
                    break;
                case CSV:
                    generateCSVReport(filePath, reportData, new String[] { "Fecha", "Trabajos Totales",
                            "Costo Promedio", "Duración Promedio", "Ingresos Totales", "Trabajos Completados" });
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported format: " + format);
            }
        } catch (IOException e) {
            log.error("Error generating preventive maintenance report", e);
            throw new RuntimeException("Failed to generate preventive maintenance report", e);
        }
    }

    private void generateCorrectiveMaintenanceReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating corrective maintenance report: {} - {}", startDate, endDate);

        try {
            List<Object[]> reportData = reportRepository.getCorrectiveMaintenanceReport(startDate, endDate);

            switch (format) {
                case PDF:
                    generateGenericPDFReport(filePath, reportData,
                            "REPORTE DE MANTENIMIENTO CORRECTIVO",
                            new String[] { "Fecha", "Trabajos Totales", "Costo Promedio", "Duración Promedio",
                                    "Ingresos Totales", "Trabajos Completados" },
                            startDate, endDate);
                    break;
                case EXCEL:
                    generateGenericExcelReport(filePath, reportData,
                            "REPORTE DE MANTENIMIENTO CORRECTIVO",
                            new String[] { "Fecha", "Trabajos Totales", "Costo Promedio", "Duración Promedio",
                                    "Ingresos Totales", "Trabajos Completados" },
                            startDate, endDate);
                    break;
                case CSV:
                    generateCSVReport(filePath, reportData, new String[] { "Fecha", "Trabajos Totales",
                            "Costo Promedio", "Duración Promedio", "Ingresos Totales", "Trabajos Completados" });
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported format: " + format);
            }
        } catch (IOException e) {
            log.error("Error generating corrective maintenance report", e);
            throw new RuntimeException("Failed to generate corrective maintenance report", e);
        }
    }

    private void generatePaymentStatusReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating payment status report: {} - {}", startDate, endDate);

        try {
            BigDecimal totalPending = reportRepository.getTotalPendingPayments();
            BigDecimal totalIncomeMonth = reportRepository.getTotalIncomeThisMonth();
            BigDecimal totalIncomeYear = reportRepository.getTotalIncomeThisYear();

            List<Object[]> reportData = new ArrayList<>();
            reportData.add(new Object[] { "Pagos Pendientes", totalPending.toString() });
            reportData.add(new Object[] { "Ingresos Este Mes", totalIncomeMonth.toString() });
            reportData.add(new Object[] { "Ingresos Este Año", totalIncomeYear.toString() });

            switch (format) {
                case PDF:
                    generateGenericPDFReport(filePath, reportData,
                            "REPORTE DE ESTADO DE PAGOS",
                            new String[] { "Concepto", "Monto" },
                            startDate, endDate);
                    break;
                case EXCEL:
                    generateGenericExcelReport(filePath, reportData,
                            "REPORTE DE ESTADO DE PAGOS",
                            new String[] { "Concepto", "Monto" },
                            startDate, endDate);
                    break;
                case CSV:
                    generateCSVReport(filePath, reportData, new String[] { "Concepto", "Monto" });
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported format: " + format);
            }
        } catch (IOException e) {
            log.error("Error generating payment status report", e);
            throw new RuntimeException("Failed to generate payment status report", e);
        }
    }

    private void generateInventoryStockReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating inventory stock report: {} - {}", startDate, endDate);

        try {
            List<Object[]> reportData = reportRepository.getLowStockAlerts();

            switch (format) {
                case PDF:
                    generateGenericPDFReport(filePath, reportData,
                            "REPORTE DE ESTADO DE INVENTARIO",
                            new String[] { "Repuesto", "Categoría", "Cantidad Disponible", "Stock Mínimo",
                                    "Precio Unitario" },
                            startDate, endDate);
                    break;
                case EXCEL:
                    generateGenericExcelReport(filePath, reportData,
                            "REPORTE DE ESTADO DE INVENTARIO",
                            new String[] { "Repuesto", "Categoría", "Cantidad Disponible", "Stock Mínimo",
                                    "Precio Unitario" },
                            startDate, endDate);
                    break;
                case CSV:
                    generateCSVReport(filePath, reportData, new String[] { "Repuesto", "Categoría",
                            "Cantidad Disponible", "Stock Mínimo", "Precio Unitario" });
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported format: " + format);
            }
        } catch (IOException e) {
            log.error("Error generating inventory stock report", e);
            throw new RuntimeException("Failed to generate inventory stock report", e);
        }
    }

    private void generateVehicleBehaviorReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating vehicle behavior report: {} - {}", startDate, endDate);

        try {
            List<Object[]> reportData = reportRepository.getVehicleBrandStatistics(startDate, endDate);

            switch (format) {
                case PDF:
                    generateGenericPDFReport(filePath, reportData,
                            "REPORTE DE COMPORTAMIENTO DE VEHÍCULOS",
                            new String[] { "Marca", "Trabajos Totales", "Costo Promedio", "Vehículos Únicos",
                                    "Ingresos Totales", "Horas Promedio" },
                            startDate, endDate);
                    break;
                case EXCEL:
                    generateGenericExcelReport(filePath, reportData,
                            "REPORTE DE COMPORTAMIENTO DE VEHÍCULOS",
                            new String[] { "Marca", "Trabajos Totales", "Costo Promedio", "Vehículos Únicos",
                                    "Ingresos Totales", "Horas Promedio" },
                            startDate, endDate);
                    break;
                case CSV:
                    generateCSVReport(filePath, reportData, new String[] { "Marca", "Trabajos Totales",
                            "Costo Promedio", "Vehículos Únicos", "Ingresos Totales", "Horas Promedio" });
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported format: " + format);
            }
        } catch (IOException e) {
            log.error("Error generating vehicle behavior report", e);
            throw new RuntimeException("Failed to generate vehicle behavior report", e);
        }
    }

    // ================================
    // MÉTODOS GENÉRICOS DE GENERACIÓN
    // ================================

    private void generateGenericPDFReport(String filePath, List<Object[]> data, String title,
            String[] headers, LocalDateTime startDate, LocalDateTime endDate) throws IOException {

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(fos);
            com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(writer);
            com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdfDoc,
                    com.itextpdf.kernel.geom.PageSize.A4);

            com.itextpdf.kernel.font.PdfFont titleFont = com.itextpdf.kernel.font.PdfFontFactory.createFont();
            com.itextpdf.kernel.font.PdfFont headerFont = com.itextpdf.kernel.font.PdfFontFactory.createFont();
            com.itextpdf.kernel.font.PdfFont normalFont = com.itextpdf.kernel.font.PdfFontFactory.createFont();

            // Título
            document.add(new com.itextpdf.layout.element.Paragraph(title)
                    .setFont(titleFont)
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                    .setMarginBottom(20));

            // Rango de fechas
            document.add(new com.itextpdf.layout.element.Paragraph(
                    String.format("Período: %s - %s",
                            startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))))
                    .setFont(normalFont)
                    .setFontSize(12)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                    .setMarginBottom(20));

            // Tabla
            if (!data.isEmpty()) {
                float[] columnWidths = new float[headers.length];
                java.util.Arrays.fill(columnWidths, 1);

                com.itextpdf.layout.element.Table table = new com.itextpdf.layout.element.Table(
                        com.itextpdf.layout.properties.UnitValue.createPercentArray(columnWidths));
                table.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));

                // Headers
                for (String header : headers) {
                    com.itextpdf.layout.element.Cell cell = new com.itextpdf.layout.element.Cell()
                            .add(new com.itextpdf.layout.element.Paragraph(header)
                                    .setFont(headerFont)
                                    .setFontColor(com.itextpdf.kernel.colors.ColorConstants.WHITE))
                            .setBackgroundColor(new com.itextpdf.kernel.colors.DeviceRgb(41, 128, 185));
                    table.addHeaderCell(cell);
                }

                // Data rows
                boolean alternateRow = false;
                for (Object[] row : data) {
                    for (int i = 0; i < row.length && i < headers.length; i++) {
                        Object cellData = row[i];
                        com.itextpdf.layout.element.Cell cell = new com.itextpdf.layout.element.Cell()
                                .add(new com.itextpdf.layout.element.Paragraph(
                                        cellData != null ? cellData.toString() : "")
                                        .setFont(normalFont));

                        if (alternateRow) {
                            cell.setBackgroundColor(new com.itextpdf.kernel.colors.DeviceRgb(245, 245, 245));
                        }
                        table.addCell(cell);
                    }
                    alternateRow = !alternateRow;
                }

                document.add(table);
            } else {
                document.add(new com.itextpdf.layout.element.Paragraph(
                        "No hay datos disponibles para el período seleccionado.")
                        .setFont(normalFont)
                        .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            }

            // Footer
            document.add(new com.itextpdf.layout.element.Paragraph(
                    String.format("Generado el: %s",
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))))
                    .setFont(normalFont)
                    .setFontSize(10)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT)
                    .setMarginTop(20));

            document.close();
        }
    }

    private void generateGenericExcelReport(String filePath, List<Object[]> data, String title,
            String[] headers, LocalDateTime startDate, LocalDateTime endDate) throws IOException {

        try (org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            org.apache.poi.xssf.usermodel.XSSFSheet sheet = workbook.createSheet("Reporte");

            int rowNum = 0;

            // Título
            org.apache.poi.ss.usermodel.Row titleRow = sheet.createRow(rowNum++);
            org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(title);

            org.apache.poi.ss.usermodel.CellStyle titleStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
            titleCell.setCellStyle(titleStyle);

            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, headers.length - 1));

            // Rango de fechas
            rowNum++;
            org.apache.poi.ss.usermodel.Row dateRow = sheet.createRow(rowNum++);
            org.apache.poi.ss.usermodel.Cell dateCell = dateRow.createCell(0);
            dateCell.setCellValue(String.format("Período: %s - %s",
                    startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(2, 2, 0, headers.length - 1));

            rowNum++;

            // Headers
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(rowNum++);
            org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);

            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data rows
            for (Object[] row : data) {
                org.apache.poi.ss.usermodel.Row dataRow = sheet.createRow(rowNum++);
                for (int i = 0; i < row.length && i < headers.length; i++) {
                    org.apache.poi.ss.usermodel.Cell cell = dataRow.createCell(i);
                    if (row[i] != null) {
                        if (row[i] instanceof Number) {
                            cell.setCellValue(((Number) row[i]).doubleValue());
                        } else {
                            cell.setCellValue(row[i].toString());
                        }
                    }
                }
            }

            // Autosize columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
        }
    }

    private void generateCSVReport(String filePath, List<Object[]> data, String[] headers) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write headers
            writer.append(String.join(",", headers));
            writer.append("\n");

            // Write data
            for (Object[] row : data) {
                List<String> stringRow = new ArrayList<>();
                for (int i = 0; i < headers.length; i++) {
                    if (i < row.length && row[i] != null) {
                        stringRow.add(row[i].toString());
                    } else {
                        stringRow.add("");
                    }
                }
                writer.append(String.join(",", stringRow));
                writer.append("\n");
            }
        }
    }

    // ================================
    // MÉTODOS UTILITARIOS
    // ================================

    private String getCurrentUsername() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception e) {
            return "system";
        }
    }

    private Long getFileSize(String filePath) {
        try {
            File file = new File(filePath);
            return file.exists() ? file.length() : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }

    private String generateReportId(ReportType reportType, ReportFormat format) {
        return String.format("%s_%s_%s",
                reportType.name(),
                format.name(),
                System.currentTimeMillis());
    }

    private String generateFileName(ReportType reportType, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        String dateRange = String.format("%s_%s",
                startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        return String.format("%s_%s%s",
                reportType.name().toLowerCase(),
                dateRange,
                format.getFileExtension());
    }

    @Override
    public List<ReportFileInfo> getAvailableReports() {
        List<ReportFileInfo> reports = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Map.Entry<String, ReportResponse> entry : reportCache.entrySet()) {
            ReportResponse report = entry.getValue();
            String filePath = reportPathCache.get(entry.getKey());

            if (filePath != null) {
                File file = new File(filePath);
                if (file.exists()) {
                    reports.add(ReportFileInfo.builder()
                            .reportId(report.getReportId())
                            .fileName(report.getFileName())
                            .reportType(report.getReportType())
                            .format(report.getFormat())
                            .fileSize(report.getFileSize())
                            .generatedAt(report.getGeneratedAt())
                            .expiresAt(report.getExpiresAt())
                            .generatedBy(report.getGeneratedBy())
                            .status(report.getStatus())
                            .downloadUrl("/api/v1/reports/download/" + report.getReportId())
                            .isExpired(report.getExpiresAt().isBefore(now))
                            .fileSizeFormatted(formatFileSize(report.getFileSize()))
                            .reportTypeDisplayName(report.getReportType().getDisplayName())
                            .formatDisplayName(report.getFormat().getDisplayName())
                            .build());
                }
            }
        }

        // Ordenar por fecha de generación (más recientes primero)
        reports.sort((r1, r2) -> r2.getGeneratedAt().compareTo(r1.getGeneratedAt()));

        return reports;
    }

    @Override
    public boolean deleteReport(String reportId) {
        try {
            String filePath = reportPathCache.remove(reportId);
            ReportResponse report = reportCache.remove(reportId);

            if (filePath != null) {
                Files.deleteIfExists(Paths.get(filePath));
            }

            log.info("Deleted report: {}", reportId);
            return true;
        } catch (Exception e) {
            log.error("Error deleting report: {}", reportId, e);
            return false;
        }
    }

    @Override
    public int cleanupExpiredReports() {
        LocalDateTime now = LocalDateTime.now();
        List<String> expiredReportIds = new ArrayList<>();

        for (Map.Entry<String, ReportResponse> entry : reportCache.entrySet()) {
            ReportResponse report = entry.getValue();
            if (report.getExpiresAt().isBefore(now)) {
                expiredReportIds.add(entry.getKey());
            }
        }

        int deletedCount = 0;
        for (String reportId : expiredReportIds) {
            if (deleteReport(reportId)) {
                deletedCount++;
            }
        }

        log.info("Cleanup completed. Deleted {} expired reports", deletedCount);
        return deletedCount;
    }

    private String formatFileSize(Long bytes) {
        if (bytes == null || bytes == 0)
            return "0 B";

        String[] units = { "B", "KB", "MB", "GB", "TB" };
        int unitIndex = 0;
        double size = bytes;

        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        return String.format("%.1f %s", size, units[unitIndex]);
    }
}