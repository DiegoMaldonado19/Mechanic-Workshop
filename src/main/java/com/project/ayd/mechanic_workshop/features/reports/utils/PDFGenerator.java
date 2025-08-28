package com.project.ayd.mechanic_workshop.features.reports.utils;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.project.ayd.mechanic_workshop.features.reports.enums.ReportType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.math.RoundingMode;

@Component
@Slf4j
public class PDFGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DeviceRgb HEADER_COLOR = new DeviceRgb(41, 128, 185);
    private static final DeviceRgb ALT_ROW_COLOR = new DeviceRgb(245, 245, 245);

    public byte[] generateReport(ReportType reportType, LocalDateTime startDate, LocalDateTime endDate)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);

        // Configurar fuentes
        PdfFont titleFont = PdfFontFactory.createFont();
        PdfFont headerFont = PdfFontFactory.createFont();
        PdfFont normalFont = PdfFontFactory.createFont();

        // Título del reporte
        addTitle(document, reportType.getDisplayName(), titleFont);
        addDateRange(document, startDate, endDate, normalFont);

        // Contenido específico por tipo de reporte
        addReportTypeContent(document, reportType, headerFont, normalFont);

        document.close();
        return baos.toByteArray();
    }

    public void generateFinancialIncomeReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) throws IOException {
        PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);

        PdfFont titleFont = PdfFontFactory.createFont();
        PdfFont headerFont = PdfFontFactory.createFont();
        PdfFont normalFont = PdfFontFactory.createFont();

        // Título
        addTitle(document, "REPORTE FINANCIERO - INGRESOS", titleFont);
        addDateRange(document, startDate, endDate, normalFont);

        // Tabla de ingresos
        float[] columnWidths = { 3, 2 };
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        // Headers
        addTableHeader(table, new String[] { "Mes", "Ingresos" }, headerFont);

        // Data
        BigDecimal total = BigDecimal.ZERO;
        boolean alternateRow = false;
        for (Object[] row : data) {
            String month = (String) row[0];
            BigDecimal income = new BigDecimal(row[1].toString());
            total = total.add(income);

            Cell monthCell = new Cell().add(new Paragraph(month).setFont(normalFont));
            Cell incomeCell = new Cell().add(
                    new Paragraph("$" + income.toString()).setFont(normalFont).setTextAlignment(TextAlignment.RIGHT));

            if (alternateRow) {
                monthCell.setBackgroundColor(ALT_ROW_COLOR);
                incomeCell.setBackgroundColor(ALT_ROW_COLOR);
            }

            table.addCell(monthCell);
            table.addCell(incomeCell);
            alternateRow = !alternateRow;
        }

        // Total row
        Cell totalLabelCell = new Cell().add(new Paragraph("TOTAL").setFont(headerFont).setBold());
        Cell totalValueCell = new Cell().add(new Paragraph("$" + total.toString()).setFont(headerFont).setBold()
                .setTextAlignment(TextAlignment.RIGHT));
        totalLabelCell.setBackgroundColor(HEADER_COLOR).setFontColor(ColorConstants.WHITE);
        totalValueCell.setBackgroundColor(HEADER_COLOR).setFontColor(ColorConstants.WHITE);

        table.addCell(totalLabelCell);
        table.addCell(totalValueCell);

        document.add(table);

        // Resumen estadístico
        document.add(new Paragraph("\n"));
        addSummarySection(document, "Resumen del Período", normalFont, headerFont);
        document.add(new Paragraph("Total de Ingresos: $" + total.toString()).setFont(normalFont));
        document.add(new Paragraph("Número de Períodos: " + data.size()).setFont(normalFont));
        if (!data.isEmpty()) {
            BigDecimal average = total.divide(new BigDecimal(data.size()), 2, RoundingMode.HALF_UP);
            document.add(new Paragraph("Promedio por Período: $" + average.toString()).setFont(normalFont));
        }

        document.close();
        log.info("PDF financial income report generated: {}", filePath);
    }

    public void generateFinancialExpensesReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) throws IOException {
        PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);

        PdfFont titleFont = PdfFontFactory.createFont();
        PdfFont headerFont = PdfFontFactory.createFont();
        PdfFont normalFont = PdfFontFactory.createFont();

        addTitle(document, "REPORTE FINANCIERO - EGRESOS", titleFont);
        addDateRange(document, startDate, endDate, normalFont);

        // Tabla de egresos
        float[] columnWidths = { 2, 2, 4 };
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        addTableHeader(table, new String[] { "Mes", "Gastos", "Descripción" }, headerFont);

        BigDecimal total = BigDecimal.ZERO;
        boolean alternateRow = false;
        for (Object[] row : data) {
            String month = (String) row[0];
            BigDecimal expense = new BigDecimal(row[1].toString());
            String description = (String) row[2];
            total = total.add(expense);

            Cell monthCell = new Cell().add(new Paragraph(month).setFont(normalFont));
            Cell expenseCell = new Cell().add(
                    new Paragraph("$" + expense.toString()).setFont(normalFont).setTextAlignment(TextAlignment.RIGHT));
            Cell descCell = new Cell().add(new Paragraph(description).setFont(normalFont));

            if (alternateRow) {
                monthCell.setBackgroundColor(ALT_ROW_COLOR);
                expenseCell.setBackgroundColor(ALT_ROW_COLOR);
                descCell.setBackgroundColor(ALT_ROW_COLOR);
            }

            table.addCell(monthCell);
            table.addCell(expenseCell);
            table.addCell(descCell);
            alternateRow = !alternateRow;
        }

        // Total row
        Cell totalLabelCell = new Cell().add(new Paragraph("TOTAL").setFont(headerFont).setBold());
        Cell totalValueCell = new Cell().add(new Paragraph("$" + total.toString()).setFont(headerFont).setBold()
                .setTextAlignment(TextAlignment.RIGHT));
        Cell emptyCell = new Cell().add(new Paragraph("").setFont(normalFont));

        totalLabelCell.setBackgroundColor(HEADER_COLOR).setFontColor(ColorConstants.WHITE);
        totalValueCell.setBackgroundColor(HEADER_COLOR).setFontColor(ColorConstants.WHITE);
        emptyCell.setBackgroundColor(HEADER_COLOR);

        table.addCell(totalLabelCell);
        table.addCell(totalValueCell);
        table.addCell(emptyCell);

        document.add(table);
        document.close();
        log.info("PDF financial expenses report generated: {}", filePath);
    }

    public void generateWorksByDateReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) throws IOException {
        PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);

        PdfFont titleFont = PdfFontFactory.createFont();
        PdfFont headerFont = PdfFontFactory.createFont();
        PdfFont normalFont = PdfFontFactory.createFont();

        addTitle(document, "REPORTE DE TRABAJOS POR FECHA", titleFont);
        addDateRange(document, startDate, endDate, normalFont);

        createSimpleTable(document, data, new String[] { "Estado", "Cantidad" }, headerFont, normalFont);
        document.close();
        log.info("PDF works by date report generated: {}", filePath);
    }

    public void generateWorksByTypeReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) throws IOException {
        PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);

        PdfFont titleFont = PdfFontFactory.createFont();
        PdfFont headerFont = PdfFontFactory.createFont();
        PdfFont normalFont = PdfFontFactory.createFont();

        addTitle(document, "REPORTE DE TRABAJOS POR TIPO DE SERVICIO", titleFont);
        addDateRange(document, startDate, endDate, normalFont);

        createSimpleTable(document, data, new String[] { "Tipo de Servicio", "Cantidad" }, headerFont, normalFont);
        document.close();
        log.info("PDF works by type report generated: {}", filePath);
    }

    public void generateWorksByEmployeeReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) throws IOException {
        PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4.rotate()); // Landscape for more columns

        PdfFont titleFont = PdfFontFactory.createFont();
        PdfFont headerFont = PdfFontFactory.createFont();
        PdfFont normalFont = PdfFontFactory.createFont();

        addTitle(document, "REPORTE DE DESEMPEÑO POR EMPLEADO", titleFont);
        addDateRange(document, startDate, endDate, normalFont);

        // Tabla más compleja para empleados
        float[] columnWidths = { 3, 1, 1.5f, 1.5f, 1.5f, 2 };
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        String[] headers = { "Empleado", "ID", "Trabajos Asignados", "Completados", "Tiempo Promedio",
                "Ingresos Totales" };
        addTableHeader(table, headers, headerFont);

        boolean alternateRow = false;
        for (Object[] row : data) {
            String employeeName = (String) row[0];
            String employeeId = row[1].toString();
            String totalWorks = row[2].toString();
            String completedWorks = row[3].toString();
            String avgTime = row[4] != null ? row[4].toString() + " hrs" : "N/A";
            String totalRevenue = "$" + (row[5] != null ? row[5].toString() : "0");

            Cell[] cells = {
                    new Cell().add(new Paragraph(employeeName).setFont(normalFont)),
                    new Cell()
                            .add(new Paragraph(employeeId).setFont(normalFont).setTextAlignment(TextAlignment.CENTER)),
                    new Cell()
                            .add(new Paragraph(totalWorks).setFont(normalFont).setTextAlignment(TextAlignment.CENTER)),
                    new Cell().add(
                            new Paragraph(completedWorks).setFont(normalFont).setTextAlignment(TextAlignment.CENTER)),
                    new Cell().add(new Paragraph(avgTime).setFont(normalFont).setTextAlignment(TextAlignment.CENTER)),
                    new Cell()
                            .add(new Paragraph(totalRevenue).setFont(normalFont).setTextAlignment(TextAlignment.RIGHT))
            };

            if (alternateRow) {
                for (Cell cell : cells) {
                    cell.setBackgroundColor(ALT_ROW_COLOR);
                }
            }

            for (Cell cell : cells) {
                table.addCell(cell);
            }
            alternateRow = !alternateRow;
        }

        document.add(table);
        document.close();
        log.info("PDF works by employee report generated: {}", filePath);
    }

    public void generatePartsUsageReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) throws IOException {
        PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);

        PdfFont titleFont = PdfFontFactory.createFont();
        PdfFont headerFont = PdfFontFactory.createFont();
        PdfFont normalFont = PdfFontFactory.createFont();

        addTitle(document, "REPORTE DE USO DE REPUESTOS", titleFont);
        addDateRange(document, startDate, endDate, normalFont);

        float[] columnWidths = { 3, 2, 1.5f, 2, 1.5f };
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        String[] headers = { "Repuesto", "Categoría", "Cantidad", "Costo Total", "Trabajos" };
        addTableHeader(table, headers, headerFont);

        boolean alternateRow = false;
        for (Object[] row : data) {
            String partName = (String) row[0];
            String category = (String) row[1];
            String quantity = row[2].toString();
            String totalCost = "$" + row[3].toString();
            String worksCount = row[4].toString();

            Cell[] cells = {
                    new Cell().add(new Paragraph(partName).setFont(normalFont)),
                    new Cell().add(new Paragraph(category).setFont(normalFont)),
                    new Cell().add(new Paragraph(quantity).setFont(normalFont).setTextAlignment(TextAlignment.CENTER)),
                    new Cell().add(new Paragraph(totalCost).setFont(normalFont).setTextAlignment(TextAlignment.RIGHT)),
                    new Cell().add(new Paragraph(worksCount).setFont(normalFont).setTextAlignment(TextAlignment.CENTER))
            };

            if (alternateRow) {
                for (Cell cell : cells) {
                    cell.setBackgroundColor(ALT_ROW_COLOR);
                }
            }

            for (Cell cell : cells) {
                table.addCell(cell);
            }
            alternateRow = !alternateRow;
        }

        document.add(table);
        document.close();
        log.info("PDF parts usage report generated: {}", filePath);
    }

    public void generatePartsByBrandReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) throws IOException {
        PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);

        PdfFont titleFont = PdfFontFactory.createFont();
        PdfFont headerFont = PdfFontFactory.createFont();
        PdfFont normalFont = PdfFontFactory.createFont();

        addTitle(document, "REPORTE DE REPUESTOS POR MARCA DE VEHÍCULO", titleFont);
        addDateRange(document, startDate, endDate, normalFont);

        float[] columnWidths = { 2, 3, 2, 1.5f, 2 };
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        String[] headers = { "Marca", "Repuesto", "Categoría", "Cantidad", "Costo Total" };
        addTableHeader(table, headers, headerFont);

        boolean alternateRow = false;
        for (Object[] row : data) {
            String brand = (String) row[0];
            String partName = (String) row[1];
            String category = (String) row[2];
            String quantity = row[3].toString();
            String totalCost = "$" + row[4].toString();

            Cell[] cells = {
                    new Cell().add(new Paragraph(brand).setFont(normalFont)),
                    new Cell().add(new Paragraph(partName).setFont(normalFont)),
                    new Cell().add(new Paragraph(category).setFont(normalFont)),
                    new Cell().add(new Paragraph(quantity).setFont(normalFont).setTextAlignment(TextAlignment.CENTER)),
                    new Cell().add(new Paragraph(totalCost).setFont(normalFont).setTextAlignment(TextAlignment.RIGHT))
            };

            if (alternateRow) {
                for (Cell cell : cells) {
                    cell.setBackgroundColor(ALT_ROW_COLOR);
                }
            }

            for (Cell cell : cells) {
                table.addCell(cell);
            }
            alternateRow = !alternateRow;
        }

        document.add(table);
        document.close();
        log.info("PDF parts by brand report generated: {}", filePath);
    }

    public void generateClientHistoryReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) throws IOException {
        PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4.rotate()); // Landscape

        PdfFont titleFont = PdfFontFactory.createFont();
        PdfFont headerFont = PdfFontFactory.createFont();
        PdfFont normalFont = PdfFontFactory.createFont();

        addTitle(document, "REPORTE DE HISTORIAL DE CLIENTES", titleFont);
        addDateRange(document, startDate, endDate, normalFont);

        float[] columnWidths = { 3, 2, 1.5f, 2, 2, 3 };
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        String[] headers = { "Cliente", "CUI", "Total Trabajos", "Total Gastado", "Última Visita",
                "Tipos de Servicio" };
        addTableHeader(table, headers, headerFont);

        boolean alternateRow = false;
        for (Object[] row : data) {
            String clientName = (String) row[0];
            String cui = (String) row[1];
            String totalWorks = row[2].toString();
            String totalSpent = "$" + row[3].toString();
            String lastVisit = row[4] != null ? row[4].toString().substring(0, 10) : "N/A";
            String serviceTypes = (String) row[5];

            Cell[] cells = {
                    new Cell().add(new Paragraph(clientName).setFont(normalFont)),
                    new Cell().add(new Paragraph(cui).setFont(normalFont)),
                    new Cell()
                            .add(new Paragraph(totalWorks).setFont(normalFont).setTextAlignment(TextAlignment.CENTER)),
                    new Cell().add(new Paragraph(totalSpent).setFont(normalFont).setTextAlignment(TextAlignment.RIGHT)),
                    new Cell().add(new Paragraph(lastVisit).setFont(normalFont).setTextAlignment(TextAlignment.CENTER)),
                    new Cell().add(new Paragraph(serviceTypes).setFont(normalFont).setFontSize(8))
            };

            if (alternateRow) {
                for (Cell cell : cells) {
                    cell.setBackgroundColor(ALT_ROW_COLOR);
                }
            }

            for (Cell cell : cells) {
                table.addCell(cell);
            }
            alternateRow = !alternateRow;
        }

        document.add(table);
        document.close();
        log.info("PDF client history report generated: {}", filePath);
    }

    public void generatePreventiveMaintenanceReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) throws IOException {
        PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);

        PdfFont titleFont = PdfFontFactory.createFont();
        PdfFont headerFont = PdfFontFactory.createFont();
        PdfFont normalFont = PdfFontFactory.createFont();

        addTitle(document, "REPORTE DE MANTENIMIENTOS PREVENTIVOS", titleFont);
        addDateRange(document, startDate, endDate, normalFont);

        float[] columnWidths = { 3, 1.5f, 2, 2, 2 };
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        String[] headers = { "Tipo de Servicio", "Total Trabajos", "Costo Promedio", "Duración Promedio",
                "Ingresos Totales" };
        addTableHeader(table, headers, headerFont);

        boolean alternateRow = false;
        for (Object[] row : data) {
            String serviceName = (String) row[0];
            String totalWorks = row[1].toString();
            String avgCost = "$" + row[2].toString();
            String avgDuration = row[3] != null ? row[3].toString() + " hrs" : "N/A";
            String totalRevenue = "$" + row[4].toString();

            Cell[] cells = {
                    new Cell().add(new Paragraph(serviceName).setFont(normalFont)),
                    new Cell()
                            .add(new Paragraph(totalWorks).setFont(normalFont).setTextAlignment(TextAlignment.CENTER)),
                    new Cell().add(new Paragraph(avgCost).setFont(normalFont).setTextAlignment(TextAlignment.RIGHT)),
                    new Cell()
                            .add(new Paragraph(avgDuration).setFont(normalFont).setTextAlignment(TextAlignment.CENTER)),
                    new Cell()
                            .add(new Paragraph(totalRevenue).setFont(normalFont).setTextAlignment(TextAlignment.RIGHT))
            };

            if (alternateRow) {
                for (Cell cell : cells) {
                    cell.setBackgroundColor(ALT_ROW_COLOR);
                }
            }

            for (Cell cell : cells) {
                table.addCell(cell);
            }
            alternateRow = !alternateRow;
        }

        document.add(table);
        document.close();
        log.info("PDF preventive maintenance report generated: {}", filePath);
    }

    // Helper methods
    private void addTitle(Document document, String title, PdfFont font) {
        Paragraph titleParagraph = new Paragraph(title)
                .setFont(font)
                .setBold()
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(titleParagraph);
    }

    private void addDateRange(Document document, LocalDateTime startDate, LocalDateTime endDate, PdfFont font) {
        String dateRange = "Período: " + formatDateRange(startDate, endDate);
        Paragraph dateParagraph = new Paragraph(dateRange)
                .setFont(font)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(dateParagraph);
    }

    private void addSummarySection(Document document, String sectionTitle, PdfFont normalFont, PdfFont headerFont) {
        Paragraph summaryTitle = new Paragraph(sectionTitle)
                .setFont(headerFont)
                .setBold()
                .setFontSize(14)
                .setMarginBottom(10);
        document.add(summaryTitle);
    }

    private void addTableHeader(Table table, String[] headers, PdfFont headerFont) {
        for (String header : headers) {
            Cell headerCell = new Cell()
                    .add(new Paragraph(header).setFont(headerFont).setBold().setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(HEADER_COLOR)
                    .setTextAlignment(TextAlignment.CENTER);
            table.addHeaderCell(headerCell);
        }
    }

    private void createSimpleTable(Document document, List<Object[]> data, String[] headers, PdfFont headerFont,
            PdfFont normalFont) {
        float[] columnWidths = new float[headers.length];
        for (int i = 0; i < headers.length; i++) {
            columnWidths[i] = 1;
        }

        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        addTableHeader(table, headers, headerFont);

        boolean alternateRow = false;
        for (Object[] row : data) {
            for (Object cellData : row) {
                Cell cell = new Cell().add(new Paragraph(cellData.toString()).setFont(normalFont));
                if (alternateRow) {
                    cell.setBackgroundColor(ALT_ROW_COLOR);
                }
                table.addCell(cell);
            }
            alternateRow = !alternateRow;
        }

        document.add(table);
    }

    private void addReportTypeContent(Document document, ReportType reportType, PdfFont headerFont,
            PdfFont normalFont) {
        // Contenido genérico para tipos de reporte sin datos específicos
        document.add(new Paragraph("Tipo de Reporte: " + reportType.getDisplayName()).setFont(normalFont)
                .setMarginBottom(10));
        document.add(new Paragraph("Generado el: " + LocalDateTime.now().format(DATE_FORMATTER)).setFont(normalFont)
                .setMarginBottom(20));

        // Agregar descripción según el tipo de reporte
        String description = getReportDescription(reportType);
        document.add(new Paragraph(description).setFont(normalFont).setMarginBottom(20));
    }

    private String getReportDescription(ReportType reportType) {
        return switch (reportType) {
            case FINANCIAL_INCOME ->
                "Este reporte muestra el análisis detallado de los ingresos del taller mecánico en el período especificado.";
            case FINANCIAL_EXPENSES -> "Este reporte presenta el desglose de gastos y egresos del taller mecánico.";
            case WORK_BY_DATE -> "Este reporte analiza la distribución de trabajos realizados por fecha.";
            case WORK_BY_TYPE -> "Este reporte clasifica los trabajos según el tipo de servicio prestado.";
            case WORK_BY_EMPLOYEE -> "Este reporte evalúa el desempeño individual de cada empleado del taller.";
            case PARTS_USAGE -> "Este reporte detalla el uso y consumo de repuestos en el período especificado.";
            case PARTS_BY_BRAND -> "Este reporte analiza el uso de repuestos clasificado por marca de vehículo.";
            case CLIENT_HISTORY -> "Este reporte presenta el historial completo de servicios por cliente.";
            case PREVENTIVE_MAINTENANCE -> "Este reporte se enfoca en los mantenimientos preventivos realizados.";
            default -> "Reporte del sistema de gestión del taller mecánico.";
        };
    }

    private String formatDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return startDate.format(DATE_FORMATTER) + " - " + endDate.format(DATE_FORMATTER);
    }
}