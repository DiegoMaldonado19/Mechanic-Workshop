package com.project.ayd.mechanic_workshop.features.reports.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Slf4j
public class CSVUtil {

    public static void writeCSV(String filePath, String[] headers, List<Object[]> data) {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write headers
            writer.write(String.join(",", headers));
            writer.write("\n");

            // Write data
            for (Object[] row : data) {
                StringBuilder line = new StringBuilder();
                for (int i = 0; i < row.length; i++) {
                    if (i > 0)
                        line.append(",");
                    String value = row[i] != null ? row[i].toString() : "";
                    // Escape commas and quotes
                    if (value.contains(",") || value.contains("\"")) {
                        value = "\"" + value.replace("\"", "\"\"") + "\"";
                    }
                    line.append(value);
                }
                writer.write(line.toString());
                writer.write("\n");
            }

            log.info("CSV file generated successfully: {}", filePath);
        } catch (IOException e) {
            log.error("Error writing CSV file: {}", filePath, e);
            throw new RuntimeException("Failed to write CSV file", e);
        }
    }
}