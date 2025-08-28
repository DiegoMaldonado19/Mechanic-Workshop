package com.project.ayd.mechanic_workshop.features.reports.utils;

import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CSVUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Escribe datos en formato CSV usando OpenCSV
     * 
     * @param filePath Ruta del archivo
     * @param headers  Encabezados de las columnas
     * @param data     Datos a escribir
     */
    public static void writeCSV(String filePath, String[] headers, List<Object[]> data) {
        try (FileWriter fileWriter = new FileWriter(filePath, StandardCharsets.UTF_8);
                CSVWriter csvWriter = new CSVWriter(fileWriter,
                        CSVWriter.DEFAULT_SEPARATOR,
                        CSVWriter.DEFAULT_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END)) {

            // Escribir encabezados
            csvWriter.writeNext(headers);

            // Escribir datos
            for (Object[] row : data) {
                String[] stringRow = new String[row.length];
                for (int i = 0; i < row.length; i++) {
                    stringRow[i] = formatValue(row[i]);
                }
                csvWriter.writeNext(stringRow);
            }

            log.info("CSV file generated successfully: {}", filePath);
        } catch (IOException e) {
            log.error("Error writing CSV file: {}", filePath, e);
            throw new RuntimeException("Failed to write CSV file", e);
        }
    }

    /**
     * Escribe datos en formato CSV con metadatos del reporte
     * 
     * @param filePath    Ruta del archivo
     * @param reportTitle Título del reporte
     * @param startDate   Fecha de inicio
     * @param endDate     Fecha de fin
     * @param headers     Encabezados de las columnas
     * @param data        Datos a escribir
     */
    public static void writeCSVWithMetadata(String filePath, String reportTitle,
            LocalDateTime startDate, LocalDateTime endDate,
            String[] headers, List<Object[]> data) {
        try (FileWriter fileWriter = new FileWriter(filePath, StandardCharsets.UTF_8);
                CSVWriter csvWriter = new CSVWriter(fileWriter,
                        CSVWriter.DEFAULT_SEPARATOR,
                        CSVWriter.DEFAULT_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END)) {

            // Escribir metadatos
            csvWriter.writeNext(new String[] { reportTitle });
            csvWriter.writeNext(new String[] { "Período", formatDateRange(startDate, endDate) });
            csvWriter.writeNext(new String[] { "Generado el", LocalDateTime.now().format(DATE_FORMATTER) });
            csvWriter.writeNext(new String[] {}); // Fila vacía

            // Escribir encabezados
            csvWriter.writeNext(headers);

            // Escribir datos
            for (Object[] row : data) {
                String[] stringRow = new String[row.length];
                for (int i = 0; i < row.length; i++) {
                    stringRow[i] = formatValue(row[i]);
                }
                csvWriter.writeNext(stringRow);
            }

            // Escribir resumen si es necesario
            if (shouldAddSummary(reportTitle)) {
                addSummaryToCSV(csvWriter, data);
            }

            log.info("CSV file with metadata generated successfully: {}", filePath);
        } catch (IOException e) {
            log.error("Error writing CSV file with metadata: {}", filePath, e);
            throw new RuntimeException("Failed to write CSV file with metadata", e);
        }
    }

    /**
     * Convierte una lista de objetos a un array CSV
     * 
     * @param data Lista de datos
     * @return Array de strings para CSV
     */
    public static List<String[]> convertToCSVFormat(List<Object[]> data) {
        List<String[]> csvData = new ArrayList<>();

        for (Object[] row : data) {
            String[] stringRow = new String[row.length];
            for (int i = 0; i < row.length; i++) {
                stringRow[i] = formatValue(row[i]);
            }
            csvData.add(stringRow);
        }

        return csvData;
    }

    /**
     * Escribe datos financieros con formato especial
     * 
     * @param filePath        Ruta del archivo
     * @param reportTitle     Título del reporte
     * @param startDate       Fecha de inicio
     * @param endDate         Fecha de fin
     * @param headers         Encabezados
     * @param data            Datos
     * @param calculateTotals Si debe calcular totales
     */
    public static void writeFinancialCSV(String filePath, String reportTitle,
            LocalDateTime startDate, LocalDateTime endDate,
            String[] headers, List<Object[]> data,
            boolean calculateTotals) {
        try (FileWriter fileWriter = new FileWriter(filePath, StandardCharsets.UTF_8);
                CSVWriter csvWriter = new CSVWriter(fileWriter)) {

            // Metadatos del reporte
            csvWriter.writeNext(new String[] { reportTitle });
            csvWriter.writeNext(new String[] { "Período", formatDateRange(startDate, endDate) });
            csvWriter.writeNext(new String[] { "Generado el", LocalDateTime.now().format(DATE_FORMATTER) });
            csvWriter.writeNext(new String[] {}); // Fila vacía

            // Encabezados
            csvWriter.writeNext(headers);

            // Datos
            double total = 0.0;
            for (Object[] row : data) {
                String[] stringRow = new String[row.length];
                for (int i = 0; i < row.length; i++) {
                    stringRow[i] = formatValue(row[i]);

                    // Sumar valores numéricos para totales (asumiendo que la segunda columna son
                    // montos)
                    if (calculateTotals && i == 1 && row[i] != null) {
                        try {
                            total += Double.parseDouble(row[i].toString());
                        } catch (NumberFormatException e) {
                            // Ignorar si no es un número
                        }
                    }
                }
                csvWriter.writeNext(stringRow);
            }

            // Agregar total si se solicitó
            if (calculateTotals) {
                csvWriter.writeNext(new String[] {}); // Fila vacía
                String[] totalRow = new String[headers.length];
                totalRow[0] = "TOTAL";
                totalRow[1] = String.format("%.2f", total);
                for (int i = 2; i < headers.length; i++) {
                    totalRow[i] = "";
                }
                csvWriter.writeNext(totalRow);
            }

            log.info("Financial CSV file generated successfully: {}", filePath);
        } catch (IOException e) {
            log.error("Error writing financial CSV file: {}", filePath, e);
            throw new RuntimeException("Failed to write financial CSV file", e);
        }
    }

    /**
     * Formatea un valor para escritura en CSV
     * 
     * @param value Valor a formatear
     * @return String formateado
     */
    private static String formatValue(Object value) {
        if (value == null) {
            return "";
        }

        String stringValue = value.toString();

        // Formatear fechas si es necesario
        if (stringValue.contains("T") && stringValue.length() > 10) {
            try {
                // Intentar formatear como fecha ISO
                return stringValue.substring(0, 10); // Solo la parte de fecha
            } catch (Exception e) {
                // Si no es una fecha válida, devolver como está
            }
        }

        // Formatear números decimales
        if (isNumeric(stringValue) && stringValue.contains(".")) {
            try {
                double number = Double.parseDouble(stringValue);
                return String.format("%.2f", number);
            } catch (NumberFormatException e) {
                // Si no es un número válido, devolver como está
            }
        }

        return stringValue;
    }

    /**
     * Verifica si una cadena es numérica
     * 
     * @param str Cadena a verificar
     * @return true si es numérica
     */
    private static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Formatea un rango de fechas
     * 
     * @param startDate Fecha de inicio
     * @param endDate   Fecha de fin
     * @return String formateado
     */
    private static String formatDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return startDate.format(DATE_FORMATTER) + " - " + endDate.format(DATE_FORMATTER);
    }

    /**
     * Determina si se debe agregar un resumen al reporte
     * 
     * @param reportTitle Título del reporte
     * @return true si debe agregar resumen
     */
    private static boolean shouldAddSummary(String reportTitle) {
        return reportTitle != null &&
                (reportTitle.toLowerCase().contains("financiero") ||
                        reportTitle.toLowerCase().contains("ingresos") ||
                        reportTitle.toLowerCase().contains("gastos"));
    }

    /**
     * Agrega un resumen al final del CSV
     * 
     * @param csvWriter Writer del CSV
     * @param data      Datos del reporte
     */
    private static void addSummaryToCSV(CSVWriter csvWriter, List<Object[]> data) {
        try {
            csvWriter.writeNext(new String[] {}); // Fila vacía
            csvWriter.writeNext(new String[] { "RESUMEN DEL REPORTE" });
            csvWriter.writeNext(new String[] { "Total de registros", String.valueOf(data.size()) });
            csvWriter.writeNext(new String[] { "Generado el", LocalDateTime.now().format(DATE_FORMATTER) });
        } catch (Exception e) {
            log.warn("Error adding summary to CSV", e);
        }
    }
}