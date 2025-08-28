package com.project.ayd.mechanic_workshop.features.reports.utils;

import com.project.ayd.mechanic_workshop.features.reports.enums.ReportFormat;
import com.project.ayd.mechanic_workshop.features.reports.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReportGenerator {

    private final ReportRepository reportRepository;
    private final PDFGenerator pdfGenerator;
    private final ExcelGenerator excelGenerator;

    public void generateFinancialIncomeReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating financial income report: {} - {}", startDate, endDate);

        List<Object[]> incomeData = reportRepository.getIncomeByMonth(startDate.toLocalDate(), endDate.toLocalDate());

        switch (format) {
            case PDF:
                pdfGenerator.generateFinancialIncomeReport(filePath, incomeData, startDate, endDate);
                break;
            case EXCEL:
                excelGenerator.generateFinancialIncomeReport(filePath, incomeData, startDate, endDate);
                break;
            case CSV:
                generateCSVFinancialIncomeReport(filePath, incomeData);
                break;
            default:
                throw new IllegalArgumentException("Unsupported format: " + format);
        }
    }

    public void generateFinancialExpensesReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating financial expenses report: {} - {}", startDate, endDate);

        // Simulated expenses data
        List<Object[]> expensesData = List.of(
                new Object[] { "2024-01", "5000.00", "Compra de repuestos" },
                new Object[] { "2024-02", "3500.00", "Gastos operativos" });

        switch (format) {
            case PDF:
                pdfGenerator.generateFinancialExpensesReport(filePath, expensesData, startDate, endDate);
                break;
            case EXCEL:
                excelGenerator.generateFinancialExpensesReport(filePath, expensesData, startDate, endDate);
                break;
            case CSV:
                generateCSVFinancialExpensesReport(filePath, expensesData);
                break;
            default:
                throw new IllegalArgumentException("Unsupported format: " + format);
        }
    }

    public void generateWorksByDateReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating works by date report: {} - {}", startDate, endDate);

        List<Object[]> worksByStatus = reportRepository.getWorksByStatus(startDate, endDate);

        switch (format) {
            case PDF:
                pdfGenerator.generateWorksByDateReport(filePath, worksByStatus, startDate, endDate);
                break;
            case EXCEL:
                excelGenerator.generateWorksByDateReport(filePath, worksByStatus, startDate, endDate);
                break;
            case CSV:
                generateCSVWorksByDateReport(filePath, worksByStatus);
                break;
            default:
                throw new IllegalArgumentException("Unsupported format: " + format);
        }
    }

    public void generateWorksByTypeReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating works by type report: {} - {}", startDate, endDate);

        List<Object[]> worksByType = reportRepository.getWorksByType(startDate, endDate);

        switch (format) {
            case PDF:
                pdfGenerator.generateWorksByTypeReport(filePath, worksByType, startDate, endDate);
                break;
            case EXCEL:
                excelGenerator.generateWorksByTypeReport(filePath, worksByType, startDate, endDate);
                break;
            case CSV:
                generateCSVWorksByTypeReport(filePath, worksByType);
                break;
            default:
                throw new IllegalArgumentException("Unsupported format: " + format);
        }
    }

    public void generateWorksByEmployeeReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating works by employee report: {} - {}", startDate, endDate);

        List<Object[]> employeePerformance = reportRepository.getEmployeePerformance(startDate, endDate);

        switch (format) {
            case PDF:
                pdfGenerator.generateWorksByEmployeeReport(filePath, employeePerformance, startDate, endDate);
                break;
            case EXCEL:
                excelGenerator.generateWorksByEmployeeReport(filePath, employeePerformance, startDate, endDate);
                break;
            case CSV:
                generateCSVWorksByEmployeeReport(filePath, employeePerformance);
                break;
            default:
                throw new IllegalArgumentException("Unsupported format: " + format);
        }
    }

    public void generatePartsUsageReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating parts usage report: {} - {}", startDate, endDate);

        List<Object[]> partUsage = reportRepository.getPartUsageStatistics(startDate, endDate);

        switch (format) {
            case PDF:
                pdfGenerator.generatePartsUsageReport(filePath, partUsage, startDate, endDate);
                break;
            case EXCEL:
                excelGenerator.generatePartsUsageReport(filePath, partUsage, startDate, endDate);
                break;
            case CSV:
                generateCSVPartsUsageReport(filePath, partUsage);
                break;
            default:
                throw new IllegalArgumentException("Unsupported format: " + format);
        }
    }

    public void generatePartsByBrandReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating parts by brand report: {} - {}", startDate, endDate);

        List<Object[]> partsByBrand = reportRepository.getPartsByVehicleBrand(startDate, endDate);

        switch (format) {
            case PDF:
                pdfGenerator.generatePartsByBrandReport(filePath, partsByBrand, startDate, endDate);
                break;
            case EXCEL:
                excelGenerator.generatePartsByBrandReport(filePath, partsByBrand, startDate, endDate);
                break;
            case CSV:
                generateCSVPartsByBrandReport(filePath, partsByBrand);
                break;
            default:
                throw new IllegalArgumentException("Unsupported format: " + format);
        }
    }

    public void generateClientHistoryReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating client history report: {} - {}", startDate, endDate);

        List<Object[]> clientHistory = reportRepository.getClientHistory(startDate, endDate);

        switch (format) {
            case PDF:
                pdfGenerator.generateClientHistoryReport(filePath, clientHistory, startDate, endDate);
                break;
            case EXCEL:
                excelGenerator.generateClientHistoryReport(filePath, clientHistory, startDate, endDate);
                break;
            case CSV:
                generateCSVClientHistoryReport(filePath, clientHistory);
                break;
            default:
                throw new IllegalArgumentException("Unsupported format: " + format);
        }
    }

    public void generatePreventiveMaintenanceReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating preventive maintenance report: {} - {}", startDate, endDate);

        List<Object[]> serviceTypeStats = reportRepository.getServiceTypeStatistics(startDate, endDate);

        switch (format) {
            case PDF:
                pdfGenerator.generatePreventiveMaintenanceReport(filePath, serviceTypeStats, startDate, endDate);
                break;
            case EXCEL:
                excelGenerator.generatePreventiveMaintenanceReport(filePath, serviceTypeStats, startDate, endDate);
                break;
            case CSV:
                generateCSVPreventiveMaintenanceReport(filePath, serviceTypeStats);
                break;
            default:
                throw new IllegalArgumentException("Unsupported format: " + format);
        }
    }

    // CSV Generation Methods (simplified implementations)
    private void generateCSVFinancialIncomeReport(String filePath, List<Object[]> data) {
        CSVUtil.writeCSV(filePath,
                new String[] { "Mes", "Ingresos" },
                data);
    }

    private void generateCSVFinancialExpensesReport(String filePath, List<Object[]> data) {
        CSVUtil.writeCSV(filePath,
                new String[] { "Mes", "Gastos", "Descripcion" },
                data);
    }

    private void generateCSVWorksByDateReport(String filePath, List<Object[]> data) {
        CSVUtil.writeCSV(filePath,
                new String[] { "Estado", "Cantidad" },
                data);
    }

    private void generateCSVWorksByTypeReport(String filePath, List<Object[]> data) {
        CSVUtil.writeCSV(filePath,
                new String[] { "Tipo de Servicio", "Cantidad" },
                data);
    }

    private void generateCSVWorksByEmployeeReport(String filePath, List<Object[]> data) {
        CSVUtil.writeCSV(filePath,
                new String[] { "Empleado", "ID", "Trabajos Asignados", "Trabajos Completados", "Tiempo Promedio",
                        "Ingresos Totales" },
                data);
    }

    private void generateCSVPartsUsageReport(String filePath, List<Object[]> data) {
        CSVUtil.writeCSV(filePath,
                new String[] { "Repuesto", "Categoria", "Cantidad Usada", "Costo Total", "Trabajos" },
                data);
    }

    private void generateCSVPartsByBrandReport(String filePath, List<Object[]> data) {
        CSVUtil.writeCSV(filePath,
                new String[] { "Marca", "Repuesto", "Categoria", "Cantidad", "Costo Total" },
                data);
    }

    private void generateCSVClientHistoryReport(String filePath, List<Object[]> data) {
        CSVUtil.writeCSV(filePath,
                new String[] { "Cliente", "CUI", "Total Trabajos", "Total Gastado", "Ultima Visita",
                        "Tipos de Servicio" },
                data);
    }

    private void generateCSVPreventiveMaintenanceReport(String filePath, List<Object[]> data) {
        CSVUtil.writeCSV(filePath,
                new String[] { "Tipo de Servicio", "Total Trabajos", "Costo Promedio", "Duracion Promedio",
                        "Ingresos Totales" },
                data);
    }
}