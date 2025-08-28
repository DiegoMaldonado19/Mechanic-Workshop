package com.project.ayd.mechanic_workshop.features.reports.utils;

import com.project.ayd.mechanic_workshop.features.reports.enums.ReportFormat;
import com.project.ayd.mechanic_workshop.features.reports.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
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

        try {
            List<Object[]> incomeData = reportRepository.getIncomeByMonth(startDate.toLocalDate(),
                    endDate.toLocalDate());

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
        } catch (IOException e) {
            log.error("Error generating financial income report", e);
            throw new RuntimeException("Failed to generate financial income report", e);
        } catch (Exception e) {
            log.error("Unexpected error generating financial income report", e);
            throw new RuntimeException("Failed to generate financial income report", e);
        }
    }

    public void generateFinancialExpensesReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating financial expenses report: {} - {}", startDate, endDate);

        try {
            // Simulated expenses data - en producción esto vendría de la base de datos
            List<Object[]> expensesData = List.of(
                    new Object[] { "2024-01", "5000.00", "Compra de repuestos" },
                    new Object[] { "2024-02", "3500.00", "Gastos operativos" },
                    new Object[] { "2024-03", "4200.00", "Mantenimiento de equipos" },
                    new Object[] { "2024-04", "2800.00", "Servicios públicos" });

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
        } catch (IOException e) {
            log.error("Error generating financial expenses report", e);
            throw new RuntimeException("Failed to generate financial expenses report", e);
        } catch (Exception e) {
            log.error("Unexpected error generating financial expenses report", e);
            throw new RuntimeException("Failed to generate financial expenses report", e);
        }
    }

    public void generateWorksByDateReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating works by date report: {} - {}", startDate, endDate);

        try {
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
        } catch (IOException e) {
            log.error("Error generating works by date report", e);
            throw new RuntimeException("Failed to generate works by date report", e);
        } catch (Exception e) {
            log.error("Unexpected error generating works by date report", e);
            throw new RuntimeException("Failed to generate works by date report", e);
        }
    }

    public void generateWorksByTypeReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating works by type report: {} - {}", startDate, endDate);

        try {
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
        } catch (IOException e) {
            log.error("Error generating works by type report", e);
            throw new RuntimeException("Failed to generate works by type report", e);
        } catch (Exception e) {
            log.error("Unexpected error generating works by type report", e);
            throw new RuntimeException("Failed to generate works by type report", e);
        }
    }

    public void generateWorksByEmployeeReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating works by employee report: {} - {}", startDate, endDate);

        try {
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
        } catch (IOException e) {
            log.error("Error generating works by employee report", e);
            throw new RuntimeException("Failed to generate works by employee report", e);
        } catch (Exception e) {
            log.error("Unexpected error generating works by employee report", e);
            throw new RuntimeException("Failed to generate works by employee report", e);
        }
    }

    public void generatePartsUsageReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating parts usage report: {} - {}", startDate, endDate);

        try {
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
        } catch (IOException e) {
            log.error("Error generating parts usage report", e);
            throw new RuntimeException("Failed to generate parts usage report", e);
        } catch (Exception e) {
            log.error("Unexpected error generating parts usage report", e);
            throw new RuntimeException("Failed to generate parts usage report", e);
        }
    }

    public void generatePartsByBrandReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating parts by brand report: {} - {}", startDate, endDate);

        try {
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
        } catch (IOException e) {
            log.error("Error generating parts by brand report", e);
            throw new RuntimeException("Failed to generate parts by brand report", e);
        } catch (Exception e) {
            log.error("Unexpected error generating parts by brand report", e);
            throw new RuntimeException("Failed to generate parts by brand report", e);
        }
    }

    public void generateClientHistoryReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating client history report: {} - {}", startDate, endDate);

        try {
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
        } catch (IOException e) {
            log.error("Error generating client history report", e);
            throw new RuntimeException("Failed to generate client history report", e);
        } catch (Exception e) {
            log.error("Unexpected error generating client history report", e);
            throw new RuntimeException("Failed to generate client history report", e);
        }
    }

    public void generatePreventiveMaintenanceReport(String filePath, ReportFormat format,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating preventive maintenance report: {} - {}", startDate, endDate);

        try {
            List<Object[]> serviceTypeStats = reportRepository.getPreventiveMaintenanceReport(startDate, endDate);

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
        } catch (IOException e) {
            log.error("Error generating preventive maintenance report", e);
            throw new RuntimeException("Failed to generate preventive maintenance report", e);
        } catch (Exception e) {
            log.error("Unexpected error generating preventive maintenance report", e);
            throw new RuntimeException("Failed to generate preventive maintenance report", e);
        }
    }

    // CSV Generation Methods
    private void generateCSVFinancialIncomeReport(String filePath, List<Object[]> data) {
        CSVUtil.writeCSV(filePath,
                new String[] { "Mes", "Ingresos" },
                data);
    }

    private void generateCSVFinancialExpensesReport(String filePath, List<Object[]> data) {
        CSVUtil.writeCSV(filePath,
                new String[] { "Mes", "Gastos", "Descripción" },
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
                new String[] { "Repuesto", "Categoría", "Cantidad Usada", "Costo Total", "Trabajos" },
                data);
    }

    private void generateCSVPartsByBrandReport(String filePath, List<Object[]> data) {
        CSVUtil.writeCSV(filePath,
                new String[] { "Marca", "Repuesto", "Categoría", "Cantidad", "Costo Total" },
                data);
    }

    private void generateCSVClientHistoryReport(String filePath, List<Object[]> data) {
        CSVUtil.writeCSV(filePath,
                new String[] { "Cliente", "CUI", "Total Trabajos", "Total Gastado", "Última Visita",
                        "Tipos de Servicio" },
                data);
    }

    private void generateCSVPreventiveMaintenanceReport(String filePath, List<Object[]> data) {
        CSVUtil.writeCSV(filePath,
                new String[] { "Tipo de Servicio", "Total Trabajos", "Costo Promedio", "Duración Promedio",
                        "Ingresos Totales" },
                data);
    }
}