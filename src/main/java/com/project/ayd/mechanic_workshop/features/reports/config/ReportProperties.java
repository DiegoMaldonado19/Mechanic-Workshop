package com.project.ayd.mechanic_workshop.features.reports.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.reports")
public class ReportProperties {

    private String tempDirectory = "temp/reports/";
    private long reportExpirationHours = 24;
    private boolean enableScheduledCleanup = true;
    private boolean enableDashboardCache = true;
    private int maxReportsPerUser = 10;
    private long maxFileSizeBytes = 50 * 1024 * 1024; // 50MB

    // Configuraciones de generación de reportes
    private PdfConfig pdf = new PdfConfig();
    private ExcelConfig excel = new ExcelConfig();

    @Data
    public static class PdfConfig {
        private String pageSize = "A4";
        private String orientation = "PORTRAIT";
        private boolean includeHeader = true;
        private boolean includeFooter = true;
        private String fontFamily = "Arial";
        private int fontSize = 10;
    }

    @Data
    public static class ExcelConfig {
        private boolean includeCharts = true;
        private boolean autoSizeColumns = true;
        private String defaultSheetName = "Reporte";
        private boolean freezeHeaders = true;
    }
}