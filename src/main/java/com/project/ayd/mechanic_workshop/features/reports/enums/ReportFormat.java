package com.project.ayd.mechanic_workshop.features.reports.enums;

public enum ReportFormat {
    PDF("PDF", "application/pdf", ".pdf"),
    EXCEL("Excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx"),
    CSV("CSV", "text/csv", ".csv"),
    IMAGE("Imagen", "image/png", ".png");

    private final String displayName;
    private final String mimeType;
    private final String fileExtension;

    ReportFormat(String displayName, String mimeType, String fileExtension) {
        this.displayName = displayName;
        this.mimeType = mimeType;
        this.fileExtension = fileExtension;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getFileExtension() {
        return fileExtension;
    }
}