package com.project.ayd.mechanic_workshop.features.reports.enums;

public enum ReportStatus {
    PENDING("Pendiente", "El reporte está en cola para generación"),
    GENERATING("Generando", "El reporte se está generando actualmente"),
    COMPLETED("Completado", "El reporte se generó exitosamente"),
    FAILED("Fallido", "Error durante la generación del reporte"),
    EXPIRED("Expirado", "El reporte ha expirado y ya no está disponible");

    private final String displayName;
    private final String description;

    ReportStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}