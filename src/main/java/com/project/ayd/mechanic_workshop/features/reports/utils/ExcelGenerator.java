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
public class ExcelGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generateReport(ReportType reportType, LocalDateTime startDate, LocalDateTime endDate) {
        // Esta implementación sería con Apache POI
        // Por ahora devolvemos contenido CSV como Excel simulado
        String content = generateReportContent(reportType, startDate, endDate);
        return content.getBytes();
    }

    public void generateFinancialIncomeReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating Excel financial income report: {}", filePath);

        StringBuilder content = new StringBuilder();
        content.append("REPORTE FINANCIERO - INGRESOS\n");
        content.append("Período,").append(formatDateRange(startDate, endDate)).append("\n\n");
        content.append("Mes,Ingresos\n");

        for (Object[] row : data) {
            content.append(row[0]).append(",").append(row[1]).append("\n");
        }

        writeExcelContent(filePath, content.toString());
    }

    public void generateFinancialExpensesReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating Excel financial expenses report: {}", filePath);

        StringBuilder content = new StringBuilder();
        content.append("REPORTE FINANCIERO - EGRESOS\n");
        content.append("Período,").append(formatDateRange(startDate, endDate)).append("\n\n");
        content.append("Mes,Gastos,Descripción\n");

        for (Object[] row : data) {
            content.append(row[0]).append(",").append(row[1]).append(",").append(row[2]).append("\n");
        }

        writeExcelContent(filePath, content.toString());
    }

    public void generateWorksByDateReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating Excel works by date report: {}", filePath);

        StringBuilder content = new StringBuilder();
        content.append("REPORTE DE TRABAJOS POR FECHA\n");
        content.append("Período,").append(formatDateRange(startDate, endDate)).append("\n\n");
        content.append("Estado,Cantidad\n");

        for (Object[] row : data) {
            content.append(row[0]).append(",").append(row[1]).append("\n");
        }

        writeExcelContent(filePath, content.toString());
    }

    public void generateWorksByTypeReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating Excel works by type report: {}", filePath);

        StringBuilder content = new StringBuilder();
        content.append("REPORTE DE TRABAJOS POR TIPO DE SERVICIO\n");
        content.append("Período,").append(formatDateRange(startDate, endDate)).append("\n\n");
        content.append("Tipo de Servicio,Cantidad\n");

        for (Object[] row : data) {
            content.append(row[0]).append(",").append(row[1]).append("\n");
        }

        writeExcelContent(filePath, content.toString());
    }

    public void generateWorksByEmployeeReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating Excel works by employee report: {}", filePath);

        StringBuilder content = new StringBuilder();
        content.append("REPORTE DE DESEMPEÑO POR EMPLEADO\n");
        content.append("Período,").append(formatDateRange(startDate, endDate)).append("\n\n");
        content.append("Empleado,ID,Trabajos Asignados,Completados,Tiempo Promedio,Ingresos Totales\n");

        for (Object[] row : data) {
            content.append(row[0]).append(",").append(row[1]).append(",").append(row[2])
                    .append(",").append(row[3]).append(",").append(row[4]).append(",").append(row[5]).append("\n");
        }

        writeExcelContent(filePath, content.toString());
    }

    public void generatePartsUsageReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating Excel parts usage report: {}", filePath);

        StringBuilder content = new StringBuilder();
        content.append("REPORTE DE USO DE REPUESTOS\n");
        content.append("Período,").append(formatDateRange(startDate, endDate)).append("\n\n");
        content.append("Repuesto,Categoría,Cantidad Usada,Costo Total,Trabajos\n");

        for (Object[] row : data) {
            content.append(row[0]).append(",").append(row[1]).append(",").append(row[2])
                    .append(",").append(row[3]).append(",").append(row[4]).append("\n");
        }

        writeExcelContent(filePath, content.toString());
    }

    public void generatePartsByBrandReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating Excel parts by brand report: {}", filePath);

        StringBuilder content = new StringBuilder();
        content.append("REPORTE DE REPUESTOS POR MARCA DE VEHÍCULO\n");
        content.append("Período,").append(formatDateRange(startDate, endDate)).append("\n\n");
        content.append("Marca,Repuesto,Categoría,Cantidad,Costo Total\n");

        for (Object[] row : data) {
            content.append(row[0]).append(",").append(row[1]).append(",").append(row[2])
                    .append(",").append(row[3]).append(",").append(row[4]).append("\n");
        }

        writeExcelContent(filePath, content.toString());
    }

    public void generateClientHistoryReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating Excel client history report: {}", filePath);

        StringBuilder content = new StringBuilder();
        content.append("REPORTE DE HISTORIAL DE CLIENTES\n");
        content.append("Período,").append(formatDateRange(startDate, endDate)).append("\n\n");
        content.append("Cliente,CUI,Total Trabajos,Total Gastado,Última Visita,Tipos de Servicio\n");

        for (Object[] row : data) {
            content.append(row[0]).append(",").append(row[1]).append(",").append(row[2])
                    .append(",").append(row[3]).append(",").append(row[4]).append(",").append(row[5]).append("\n");
        }

        writeExcelContent(filePath, content.toString());
    }

    public void generatePreventiveMaintenanceReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating Excel preventive maintenance report: {}", filePath);

        StringBuilder content = new StringBuilder();
        content.append("REPORTE DE MANTENIMIENTOS PREVENTIVOS\n");
        content.append("Período,").append(formatDateRange(startDate, endDate)).append("\n\n");
        content.append("Tipo de Servicio,Total Trabajos,Costo Promedio,Duración Promedio,Ingresos Totales\n");

        for (Object[] row : data) {
            content.append(row[0]).append(",").append(row[1]).append(",").append(row[2])
                    .append(",").append(row[3]).append(",").append(row[4]).append("\n");
        }

        writeExcelContent(filePath, content.toString());
    }

    private String generateReportContent(ReportType reportType, LocalDateTime startDate, LocalDateTime endDate) {
        return String.format("REPORTE,%s\nPeríodo,%s\nGenerado,%s\n\n[Contenido del reporte en formato CSV...]",
                reportType.getDisplayName(),
                formatDateRange(startDate, endDate),
                LocalDateTime.now().format(DATE_FORMATTER));
    }

    private void writeExcelContent(String filePath, String content) {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            // En una implementación real, aquí usaríamos Apache POI para generar Excel
            // Por ahora, escribimos el contenido como CSV que puede abrir Excel
            fos.write(content.getBytes());
            log.info("Excel report generated successfully: {}", filePath);
        } catch (IOException e) {
            log.error("Error writing Excel file: {}", filePath, e);
            throw new RuntimeException("Failed to write Excel file", e);
        }
    }

    private String formatDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return startDate.format(DATE_FORMATTER) + " - " + endDate.format(DATE_FORMATTER);
    }
}