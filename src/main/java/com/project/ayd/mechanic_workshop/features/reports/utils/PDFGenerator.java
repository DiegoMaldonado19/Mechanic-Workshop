package com.project.ayd.mechanic_workshop.features.reports.utils;

import com.project.ayd.mechanic_workshop.features.reports.enums.ReportType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Slf4j
public class PDFGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generateReport(ReportType reportType, LocalDateTime startDate, LocalDateTime endDate) {
        // Esta implementación sería con una librería como iText o similar
        // Por ahora devolvemos un PDF simulado
        String content = generateReportContent(reportType, startDate, endDate);
        return content.getBytes(); // En producción, esto generaría un PDF real
    }

    public void generateFinancialIncomeReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating PDF financial income report: {}", filePath);

        StringBuilder content = new StringBuilder();
        content.append("REPORTE FINANCIERO - INGRESOS\n");
        content.append("Período: ").append(formatDateRange(startDate, endDate)).append("\n\n");
        content.append("Mes\t\tIngresos\n");
        content.append("========================\n");

        for (Object[] row : data) {
            content.append(row[0]).append("\t\t").append(row[1]).append("\n");
        }

        writePDFContent(filePath, content.toString());
    }

    public void generateFinancialExpensesReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating PDF financial expenses report: {}", filePath);

        StringBuilder content = new StringBuilder();
        content.append("REPORTE FINANCIERO - EGRESOS\n");
        content.append("Período: ").append(formatDateRange(startDate, endDate)).append("\n\n");
        content.append("Mes\t\tGastos\t\tDescripción\n");
        content.append("================================================\n");

        for (Object[] row : data) {
            content.append(row[0]).append("\t\t").append(row[1]).append("\t\t").append(row[2]).append("\n");
        }

        writePDFContent(filePath, content.toString());
    }

    public void generateWorksByDateReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating PDF works by date report: {}", filePath);

        StringBuilder content = new StringBuilder();
        content.append("REPORTE DE TRABAJOS POR FECHA\n");
        content.append("Período: ").append(formatDateRange(startDate, endDate)).append("\n\n");
        content.append("Estado\t\tCantidad\n");
        content.append("========================\n");

        for (Object[] row : data) {
            content.append(row[0]).append("\t\t").append(row[1]).append("\n");
        }

        writePDFContent(filePath, content.toString());
    }

    public void generateWorksByTypeReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating PDF works by type report: {}", filePath);

        StringBuilder content = new StringBuilder();
        content.append("REPORTE DE TRABAJOS POR TIPO DE SERVICIO\n");
        content.append("Período: ").append(formatDateRange(startDate, endDate)).append("\n\n");
        content.append("Tipo de Servicio\t\tCantidad\n");
        content.append("=====================================\n");

        for (Object[] row : data) {
            content.append(row[0]).append("\t\t").append(row[1]).append("\n");
        }

        writePDFContent(filePath, content.toString());
    }

    public void generateWorksByEmployeeReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating PDF works by employee report: {}", filePath);

        StringBuilder content = new StringBuilder();
        content.append("REPORTE DE DESEMPEÑO POR EMPLEADO\n");
        content.append("Período: ").append(formatDateRange(startDate, endDate)).append("\n\n");
        content.append("Empleado\t\tTrabajos\t\tCompletados\t\tIngresos\n");
        content.append("=======================================================\n");

        for (Object[] row : data) {
            content.append(row[0]).append("\t\t").append(row[2]).append("\t\t")
                    .append(row[3]).append("\t\t").append(row[5]).append("\n");
        }

        writePDFContent(filePath, content.toString());
    }

    public void generatePartsUsageReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating PDF parts usage report: {}", filePath);

        StringBuilder content = new StringBuilder();
        content.append("REPORTE DE USO DE REPUESTOS\n");
        content.append("Período: ").append(formatDateRange(startDate, endDate)).append("\n\n");
        content.append("Repuesto\t\tCategoría\t\tCantidad\t\tCosto Total\n");
        content.append("=========================================================\n");

        for (Object[] row : data) {
            content.append(row[0]).append("\t\t").append(row[1]).append("\t\t")
                    .append(row[2]).append("\t\t").append(row[3]).append("\n");
        }

        writePDFContent(filePath, content.toString());
    }

    public void generatePartsByBrandReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating PDF parts by brand report: {}", filePath);

        StringBuilder content = new StringBuilder();
        content.append("REPORTE DE REPUESTOS POR MARCA DE VEHÍCULO\n");
        content.append("Período: ").append(formatDateRange(startDate, endDate)).append("\n\n");
        content.append("Marca\t\tRepuesto\t\tCantidad\t\tCosto\n");
        content.append("================================================\n");

        for (Object[] row : data) {
            content.append(row[0]).append("\t\t").append(row[1]).append("\t\t")
                    .append(row[3]).append("\t\t").append(row[4]).append("\n");
        }

        writePDFContent(filePath, content.toString());
    }

    public void generateClientHistoryReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating PDF client history report: {}", filePath);

        StringBuilder content = new StringBuilder();
        content.append("REPORTE DE HISTORIAL DE CLIENTES\n");
        content.append("Período: ").append(formatDateRange(startDate, endDate)).append("\n\n");
        content.append("Cliente\t\tTrabajos\t\tTotal Gastado\t\tÚltima Visita\n");
        content.append("========================================================\n");

        for (Object[] row : data) {
            content.append(row[0]).append("\t\t").append(row[2]).append("\t\t")
                    .append(row[3]).append("\t\t").append(row[4]).append("\n");
        }

        writePDFContent(filePath, content.toString());
    }

    public void generatePreventiveMaintenanceReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating PDF preventive maintenance report: {}", filePath);

        StringBuilder content = new StringBuilder();
        content.append("REPORTE DE MANTENIMIENTOS PREVENTIVOS\n");
        content.append("Período: ").append(formatDateRange(startDate, endDate)).append("\n\n");
        content.append("Servicio\t\tTrabajos\t\tCosto Promedio\t\tIngresos\n");
        content.append("=======================================================\n");

        for (Object[] row : data) {
            content.append(row[0]).append("\t\t").append(row[1]).append("\t\t")
                    .append(row[2]).append("\t\t").append(row[4]).append("\n");
        }

        writePDFContent(filePath, content.toString());
    }

    private String generateReportContent(ReportType reportType, LocalDateTime startDate, LocalDateTime endDate) {
        return String.format("REPORTE: %s\nPeríodo: %s\nGenerado: %s\n\n[Contenido del reporte...]",
                reportType.getDisplayName(),
                formatDateRange(startDate, endDate),
                LocalDateTime.now().format(DATE_FORMATTER));
    }

    private void writePDFContent(String filePath, String content) {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            // En una implementación real, aquí usaríamos iText o similar para generar PDF
            // Por ahora, escribimos el contenido como texto plano
            fos.write(content.getBytes());
            log.info("PDF report generated successfully: {}", filePath);
        } catch (IOException e) {
            log.error("Error writing PDF file: {}", filePath, e);
            throw new RuntimeException("Failed to write PDF file", e);
        }
    }

    private String formatDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return startDate.format(DATE_FORMATTER) + " - " + endDate.format(DATE_FORMATTER);
    }
}