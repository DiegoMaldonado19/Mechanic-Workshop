package com.project.ayd.mechanic_workshop.features.reports.utils;

import com.project.ayd.mechanic_workshop.features.reports.enums.ReportType;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
public class ExcelGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generateReport(ReportType reportType, LocalDateTime startDate, LocalDateTime endDate)
            throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Reporte");

        // Crear estilos
        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dateStyle = createDateStyle(workbook);

        // Título del reporte
        createTitle(sheet, reportType.getDisplayName(), titleStyle);
        createDateRange(sheet, startDate, endDate, dateStyle);

        // Contenido específico por tipo de reporte
        addReportTypeContent(sheet, reportType, headerStyle);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        return baos.toByteArray();
    }

    public void generateFinancialIncomeReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Ingresos Financieros");

        // Crear estilos
        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dateStyle = createDateStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);
        CellStyle alternateRowStyle = createAlternateRowStyle(workbook);
        CellStyle totalStyle = createTotalStyle(workbook);

        int rowIndex = 0;

        // Título
        Row titleRow = sheet.createRow(rowIndex++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("REPORTE FINANCIERO - INGRESOS");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));

        // Fecha
        rowIndex++; // Fila vacía
        Row dateRow = sheet.createRow(rowIndex++);
        Cell dateCell = dateRow.createCell(0);
        dateCell.setCellValue("Período: " + formatDateRange(startDate, endDate));
        dateCell.setCellStyle(dateStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowIndex - 1, rowIndex - 1, 0, 1));

        rowIndex++; // Fila vacía

        // Headers
        Row headerRow = sheet.createRow(rowIndex++);
        Cell monthHeader = headerRow.createCell(0);
        monthHeader.setCellValue("Mes");
        monthHeader.setCellStyle(headerStyle);

        Cell incomeHeader = headerRow.createCell(1);
        incomeHeader.setCellValue("Ingresos");
        incomeHeader.setCellStyle(headerStyle);

        // Data rows
        BigDecimal total = BigDecimal.ZERO;
        boolean alternateRow = false;
        for (Object[] row : data) {
            Row dataRow = sheet.createRow(rowIndex++);

            String month = (String) row[0];
            BigDecimal income = new BigDecimal(row[1].toString());
            total = total.add(income);

            Cell monthCell = dataRow.createCell(0);
            monthCell.setCellValue(month);
            if (alternateRow) {
                monthCell.setCellStyle(alternateRowStyle);
            }

            Cell incomeCell = dataRow.createCell(1);
            incomeCell.setCellValue(income.doubleValue());
            CellStyle incomeStyle = alternateRow ? createAlternateCurrencyStyle(workbook) : currencyStyle;
            incomeCell.setCellStyle(incomeStyle);

            alternateRow = !alternateRow;
        }

        // Total row
        Row totalRow = sheet.createRow(rowIndex++);
        Cell totalLabelCell = totalRow.createCell(0);
        totalLabelCell.setCellValue("TOTAL");
        totalLabelCell.setCellStyle(totalStyle);

        Cell totalValueCell = totalRow.createCell(1);
        totalValueCell.setCellValue(total.doubleValue());
        totalValueCell.setCellStyle(totalStyle);

        // Resumen estadístico
        rowIndex++; // Fila vacía
        Row summaryTitleRow = sheet.createRow(rowIndex++);
        Cell summaryTitleCell = summaryTitleRow.createCell(0);
        summaryTitleCell.setCellValue("RESUMEN DEL PERÍODO");
        summaryTitleCell.setCellStyle(headerStyle);

        Row totalSummaryRow = sheet.createRow(rowIndex++);
        totalSummaryRow.createCell(0).setCellValue("Total de Ingresos:");
        Cell totalSummaryCell = totalSummaryRow.createCell(1);
        totalSummaryCell.setCellValue(total.doubleValue());
        totalSummaryCell.setCellStyle(currencyStyle);

        Row periodCountRow = sheet.createRow(rowIndex++);
        periodCountRow.createCell(0).setCellValue("Número de Períodos:");
        periodCountRow.createCell(1).setCellValue(data.size());

        if (!data.isEmpty()) {
            BigDecimal average = total.divide(new BigDecimal(data.size()), 2, RoundingMode.HALF_UP);
            Row averageRow = sheet.createRow(rowIndex++);
            averageRow.createCell(0).setCellValue("Promedio por Período:");
            Cell averageCell = averageRow.createCell(1);
            averageCell.setCellValue(average.doubleValue());
            averageCell.setCellStyle(currencyStyle);
        }

        // Auto-ajustar columnas
        autoSizeColumns(sheet, 2);

        // Guardar archivo
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }
        workbook.close();
        log.info("Excel financial income report generated: {}", filePath);
    }

    public void generateFinancialExpensesReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Egresos Financieros");

        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dateStyle = createDateStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);
        CellStyle alternateRowStyle = createAlternateRowStyle(workbook);

        int rowIndex = 0;

        // Título y fecha
        createTitle(sheet, "REPORTE FINANCIERO - EGRESOS", titleStyle, rowIndex++, 3);
        rowIndex++; // Fila vacía
        createDateRange(sheet, startDate, endDate, dateStyle, rowIndex++, 3);
        rowIndex++; // Fila vacía

        // Headers
        Row headerRow = sheet.createRow(rowIndex++);
        String[] headers = { "Mes", "Gastos", "Descripción" };
        for (int i = 0; i < headers.length; i++) {
            Cell headerCell = headerRow.createCell(i);
            headerCell.setCellValue(headers[i]);
            headerCell.setCellStyle(headerStyle);
        }

        // Data
        BigDecimal total = BigDecimal.ZERO;
        boolean alternateRow = false;
        for (Object[] row : data) {
            Row dataRow = sheet.createRow(rowIndex++);

            String month = (String) row[0];
            BigDecimal expense = new BigDecimal(row[1].toString());
            String description = (String) row[2];
            total = total.add(expense);

            // Mes
            Cell monthCell = dataRow.createCell(0);
            monthCell.setCellValue(month);
            if (alternateRow) {
                monthCell.setCellStyle(alternateRowStyle);
            }

            // Gastos
            Cell expenseCell = dataRow.createCell(1);
            expenseCell.setCellValue(expense.doubleValue());
            CellStyle expenseStyle = alternateRow ? createAlternateCurrencyStyle(workbook) : currencyStyle;
            expenseCell.setCellStyle(expenseStyle);

            // Descripción
            Cell descCell = dataRow.createCell(2);
            descCell.setCellValue(description);
            if (alternateRow) {
                descCell.setCellStyle(alternateRowStyle);
            }

            alternateRow = !alternateRow;
        }

        // Total
        CellStyle totalStyle = createTotalStyle(workbook);
        Row totalRow = sheet.createRow(rowIndex++);
        totalRow.createCell(0).setCellValue("TOTAL");
        totalRow.getCell(0).setCellStyle(totalStyle);
        Cell totalCell = totalRow.createCell(1);
        totalCell.setCellValue(total.doubleValue());
        totalCell.setCellStyle(totalStyle);

        autoSizeColumns(sheet, 3);

        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }
        workbook.close();
        log.info("Excel financial expenses report generated: {}", filePath);
    }

    public void generateWorksByDateReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) throws IOException {
        createSimpleReport(filePath, "Trabajos por Fecha", "REPORTE DE TRABAJOS POR FECHA",
                startDate, endDate, data, new String[] { "Estado", "Cantidad" });
    }

    public void generateWorksByTypeReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) throws IOException {
        createSimpleReport(filePath, "Trabajos por Tipo", "REPORTE DE TRABAJOS POR TIPO DE SERVICIO",
                startDate, endDate, data, new String[] { "Tipo de Servicio", "Cantidad" });
    }

    public void generateWorksByEmployeeReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Desempeño Empleados");

        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dateStyle = createDateStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);
        CellStyle alternateRowStyle = createAlternateRowStyle(workbook);

        int rowIndex = 0;

        // Título y fecha
        createTitle(sheet, "REPORTE DE DESEMPEÑO POR EMPLEADO", titleStyle, rowIndex++, 6);
        rowIndex++; // Fila vacía
        createDateRange(sheet, startDate, endDate, dateStyle, rowIndex++, 6);
        rowIndex++; // Fila vacía

        // Headers
        Row headerRow = sheet.createRow(rowIndex++);
        String[] headers = { "Empleado", "ID", "Trabajos Asignados", "Completados", "Tiempo Promedio",
                "Ingresos Totales" };
        for (int i = 0; i < headers.length; i++) {
            Cell headerCell = headerRow.createCell(i);
            headerCell.setCellValue(headers[i]);
            headerCell.setCellStyle(headerStyle);
        }

        // Data
        boolean alternateRow = false;
        for (Object[] row : data) {
            Row dataRow = sheet.createRow(rowIndex++);

            String employeeName = (String) row[0];
            String employeeId = row[1].toString();
            Number totalWorks = (Number) row[2];
            Number completedWorks = (Number) row[3];
            Number avgTime = (Number) row[4];
            Number totalRevenue = (Number) row[5];

            CellStyle rowStyle = alternateRow ? alternateRowStyle : null;

            createDataCell(dataRow, 0, employeeName, rowStyle);
            createDataCell(dataRow, 1, employeeId, rowStyle);
            createDataCell(dataRow, 2, totalWorks.intValue(), rowStyle);
            createDataCell(dataRow, 3, completedWorks.intValue(), rowStyle);
            createDataCell(dataRow, 4, avgTime != null ? avgTime.doubleValue() : 0.0, rowStyle);

            Cell revenueCell = dataRow.createCell(5);
            revenueCell.setCellValue(totalRevenue != null ? totalRevenue.doubleValue() : 0.0);
            revenueCell.setCellStyle(alternateRow ? createAlternateCurrencyStyle(workbook) : currencyStyle);

            alternateRow = !alternateRow;
        }

        autoSizeColumns(sheet, 6);

        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }
        workbook.close();
        log.info("Excel works by employee report generated: {}", filePath);
    }

    public void generatePartsUsageReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Uso de Repuestos");

        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dateStyle = createDateStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);
        CellStyle alternateRowStyle = createAlternateRowStyle(workbook);

        int rowIndex = 0;

        // Título y fecha
        createTitle(sheet, "REPORTE DE USO DE REPUESTOS", titleStyle, rowIndex++, 5);
        rowIndex++; // Fila vacía
        createDateRange(sheet, startDate, endDate, dateStyle, rowIndex++, 5);
        rowIndex++; // Fila vacía

        // Headers
        Row headerRow = sheet.createRow(rowIndex++);
        String[] headers = { "Repuesto", "Categoría", "Cantidad Usada", "Costo Total", "Trabajos" };
        for (int i = 0; i < headers.length; i++) {
            Cell headerCell = headerRow.createCell(i);
            headerCell.setCellValue(headers[i]);
            headerCell.setCellStyle(headerStyle);
        }

        // Data
        boolean alternateRow = false;
        for (Object[] row : data) {
            Row dataRow = sheet.createRow(rowIndex++);

            String partName = (String) row[0];
            String category = (String) row[1];
            Number quantity = (Number) row[2];
            Number totalCost = (Number) row[3];
            Number worksCount = (Number) row[4];

            CellStyle rowStyle = alternateRow ? alternateRowStyle : null;

            createDataCell(dataRow, 0, partName, rowStyle);
            createDataCell(dataRow, 1, category, rowStyle);
            createDataCell(dataRow, 2, quantity.intValue(), rowStyle);

            Cell costCell = dataRow.createCell(3);
            costCell.setCellValue(totalCost.doubleValue());
            costCell.setCellStyle(alternateRow ? createAlternateCurrencyStyle(workbook) : currencyStyle);

            createDataCell(dataRow, 4, worksCount.intValue(), rowStyle);

            alternateRow = !alternateRow;
        }

        autoSizeColumns(sheet, 5);

        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }
        workbook.close();
        log.info("Excel parts usage report generated: {}", filePath);
    }

    public void generatePartsByBrandReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Repuestos por Marca");

        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dateStyle = createDateStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);
        CellStyle alternateRowStyle = createAlternateRowStyle(workbook);

        int rowIndex = 0;

        // Título y fecha
        createTitle(sheet, "REPORTE DE REPUESTOS POR MARCA DE VEHÍCULO", titleStyle, rowIndex++, 5);
        rowIndex++; // Fila vacía
        createDateRange(sheet, startDate, endDate, dateStyle, rowIndex++, 5);
        rowIndex++; // Fila vacía

        // Headers
        Row headerRow = sheet.createRow(rowIndex++);
        String[] headers = { "Marca", "Repuesto", "Categoría", "Cantidad", "Costo Total" };
        for (int i = 0; i < headers.length; i++) {
            Cell headerCell = headerRow.createCell(i);
            headerCell.setCellValue(headers[i]);
            headerCell.setCellStyle(headerStyle);
        }

        // Data
        boolean alternateRow = false;
        for (Object[] row : data) {
            Row dataRow = sheet.createRow(rowIndex++);

            String brand = (String) row[0];
            String partName = (String) row[1];
            String category = (String) row[2];
            Number quantity = (Number) row[3];
            Number totalCost = (Number) row[4];

            CellStyle rowStyle = alternateRow ? alternateRowStyle : null;

            createDataCell(dataRow, 0, brand, rowStyle);
            createDataCell(dataRow, 1, partName, rowStyle);
            createDataCell(dataRow, 2, category, rowStyle);
            createDataCell(dataRow, 3, quantity.intValue(), rowStyle);

            Cell costCell = dataRow.createCell(4);
            costCell.setCellValue(totalCost.doubleValue());
            costCell.setCellStyle(alternateRow ? createAlternateCurrencyStyle(workbook) : currencyStyle);

            alternateRow = !alternateRow;
        }

        autoSizeColumns(sheet, 5);

        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }
        workbook.close();
        log.info("Excel parts by brand report generated: {}", filePath);
    }

    public void generateClientHistoryReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Historial Clientes");

        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dateStyle = createDateStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);
        CellStyle alternateRowStyle = createAlternateRowStyle(workbook);

        int rowIndex = 0;

        // Título y fecha
        createTitle(sheet, "REPORTE DE HISTORIAL DE CLIENTES", titleStyle, rowIndex++, 6);
        rowIndex++; // Fila vacía
        createDateRange(sheet, startDate, endDate, dateStyle, rowIndex++, 6);
        rowIndex++; // Fila vacía

        // Headers
        Row headerRow = sheet.createRow(rowIndex++);
        String[] headers = { "Cliente", "CUI", "Total Trabajos", "Total Gastado", "Última Visita",
                "Tipos de Servicio" };
        for (int i = 0; i < headers.length; i++) {
            Cell headerCell = headerRow.createCell(i);
            headerCell.setCellValue(headers[i]);
            headerCell.setCellStyle(headerStyle);
        }

        // Data
        boolean alternateRow = false;
        for (Object[] row : data) {
            Row dataRow = sheet.createRow(rowIndex++);

            String clientName = (String) row[0];
            String cui = (String) row[1];
            Number totalWorks = (Number) row[2];
            Number totalSpent = (Number) row[3];
            String lastVisit = row[4] != null ? row[4].toString().substring(0, 10) : "N/A";
            String serviceTypes = (String) row[5];

            CellStyle rowStyle = alternateRow ? alternateRowStyle : null;

            createDataCell(dataRow, 0, clientName, rowStyle);
            createDataCell(dataRow, 1, cui, rowStyle);
            createDataCell(dataRow, 2, totalWorks.intValue(), rowStyle);

            Cell spentCell = dataRow.createCell(3);
            spentCell.setCellValue(totalSpent.doubleValue());
            spentCell.setCellStyle(alternateRow ? createAlternateCurrencyStyle(workbook) : currencyStyle);

            createDataCell(dataRow, 4, lastVisit, rowStyle);
            createDataCell(dataRow, 5, serviceTypes, rowStyle);

            alternateRow = !alternateRow;
        }

        autoSizeColumns(sheet, 6);

        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }
        workbook.close();
        log.info("Excel client history report generated: {}", filePath);
    }

    public void generatePreventiveMaintenanceReport(String filePath, List<Object[]> data,
            LocalDateTime startDate, LocalDateTime endDate) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Mantenimiento Preventivo");

        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dateStyle = createDateStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);
        CellStyle alternateRowStyle = createAlternateRowStyle(workbook);

        int rowIndex = 0;

        // Título y fecha
        createTitle(sheet, "REPORTE DE MANTENIMIENTOS PREVENTIVOS", titleStyle, rowIndex++, 5);
        rowIndex++; // Fila vacía
        createDateRange(sheet, startDate, endDate, dateStyle, rowIndex++, 5);
        rowIndex++; // Fila vacía

        // Headers
        Row headerRow = sheet.createRow(rowIndex++);
        String[] headers = { "Tipo de Servicio", "Total Trabajos", "Costo Promedio", "Duración Promedio",
                "Ingresos Totales" };
        for (int i = 0; i < headers.length; i++) {
            Cell headerCell = headerRow.createCell(i);
            headerCell.setCellValue(headers[i]);
            headerCell.setCellStyle(headerStyle);
        }

        // Data
        boolean alternateRow = false;
        for (Object[] row : data) {
            Row dataRow = sheet.createRow(rowIndex++);

            String serviceName = (String) row[0];
            Number totalWorks = (Number) row[1];
            Number avgCost = (Number) row[2];
            Number avgDuration = (Number) row[3];
            Number totalRevenue = (Number) row[4];

            CellStyle rowStyle = alternateRow ? alternateRowStyle : null;

            createDataCell(dataRow, 0, serviceName, rowStyle);
            createDataCell(dataRow, 1, totalWorks.intValue(), rowStyle);

            Cell avgCostCell = dataRow.createCell(2);
            avgCostCell.setCellValue(avgCost.doubleValue());
            avgCostCell.setCellStyle(alternateRow ? createAlternateCurrencyStyle(workbook) : currencyStyle);

            createDataCell(dataRow, 3, avgDuration != null ? avgDuration.doubleValue() : 0.0, rowStyle);

            Cell revenueCell = dataRow.createCell(4);
            revenueCell.setCellValue(totalRevenue.doubleValue());
            revenueCell.setCellStyle(alternateRow ? createAlternateCurrencyStyle(workbook) : currencyStyle);

            alternateRow = !alternateRow;
        }

        autoSizeColumns(sheet, 5);

        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }
        workbook.close();
        log.info("Excel preventive maintenance report generated: {}", filePath);
    }

    // Helper methods
    private void createSimpleReport(String filePath, String sheetName, String title,
            LocalDateTime startDate, LocalDateTime endDate,
            List<Object[]> data, String[] headers) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);

        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dateStyle = createDateStyle(workbook);
        CellStyle alternateRowStyle = createAlternateRowStyle(workbook);

        int rowIndex = 0;

        // Título y fecha
        createTitle(sheet, title, titleStyle, rowIndex++, headers.length);
        rowIndex++; // Fila vacía
        createDateRange(sheet, startDate, endDate, dateStyle, rowIndex++, headers.length);
        rowIndex++; // Fila vacía

        // Headers
        Row headerRow = sheet.createRow(rowIndex++);
        for (int i = 0; i < headers.length; i++) {
            Cell headerCell = headerRow.createCell(i);
            headerCell.setCellValue(headers[i]);
            headerCell.setCellStyle(headerStyle);
        }

        // Data
        boolean alternateRow = false;
        for (Object[] row : data) {
            Row dataRow = sheet.createRow(rowIndex++);
            for (int i = 0; i < row.length && i < headers.length; i++) {
                createDataCell(dataRow, i, row[i].toString(), alternateRow ? alternateRowStyle : null);
            }
            alternateRow = !alternateRow;
        }

        autoSizeColumns(sheet, headers.length);

        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }
        workbook.close();
        log.info("Excel simple report generated: {}", filePath);
    }

    private void createTitle(Sheet sheet, String title, CellStyle titleStyle, int rowIndex, int colSpan) {
        Row titleRow = sheet.createRow(rowIndex);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(title);
        titleCell.setCellStyle(titleStyle);
        if (colSpan > 1) {
            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, colSpan - 1));
        }
    }

    private void createTitle(Sheet sheet, String title, CellStyle titleStyle) {
        createTitle(sheet, title, titleStyle, 0, 2);
    }

    private void createDateRange(Sheet sheet, LocalDateTime startDate, LocalDateTime endDate,
            CellStyle dateStyle, int rowIndex, int colSpan) {
        Row dateRow = sheet.createRow(rowIndex);
        Cell dateCell = dateRow.createCell(0);
        dateCell.setCellValue("Período: " + formatDateRange(startDate, endDate));
        dateCell.setCellStyle(dateStyle);
        if (colSpan > 1) {
            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, colSpan - 1));
        }
    }

    private void createDateRange(Sheet sheet, LocalDateTime startDate, LocalDateTime endDate, CellStyle dateStyle) {
        createDateRange(sheet, startDate, endDate, dateStyle, 2, 2);
    }

    private void createDataCell(Row row, int colIndex, Object value, CellStyle style) {
        Cell cell = row.createCell(colIndex);

        if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else {
            cell.setCellValue(value != null ? value.toString() : "");
        }

        if (style != null) {
            cell.setCellStyle(style);
        }
    }

    private void autoSizeColumns(Sheet sheet, int numColumns) {
        for (int i = 0; i < numColumns; i++) {
            sheet.autoSizeColumn(i);
            // Agregar un poco de padding
            int currentWidth = sheet.getColumnWidth(i);
            sheet.setColumnWidth(i, currentWidth + 1000);
        }
    }

    private void addReportTypeContent(Sheet sheet, ReportType reportType, CellStyle headerStyle) {
        // Agregar contenido específico según el tipo de reporte
        Row contentRow = sheet.createRow(5);
        Cell contentCell = contentRow.createCell(0);
        contentCell.setCellValue("Tipo de Reporte: " + reportType.getDisplayName());
        contentCell.setCellStyle(headerStyle);
    }

    // Estilos
    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        font.setColor(IndexedColors.BLACK.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        font.setItalic(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("$#,##0.00"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private CellStyle createAlternateRowStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createAlternateCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("$#,##0.00"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createTotalStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("$#,##0.00"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private String formatDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return startDate.format(DATE_FORMATTER) + " - " + endDate.format(DATE_FORMATTER);
    }
}