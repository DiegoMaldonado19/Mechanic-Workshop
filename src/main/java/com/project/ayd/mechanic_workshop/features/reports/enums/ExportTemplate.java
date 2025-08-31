package com.project.ayd.mechanic_workshop.features.reports.enums;

public enum ExportTemplate {
    SIMPLE("Simple", "Plantilla básica con datos tabulares"),
    DETAILED("Detallado", "Plantilla con gráficos y análisis detallado"),
    EXECUTIVE("Ejecutivo", "Plantilla para presentación ejecutiva"),
    TECHNICAL("Técnico", "Plantilla con información técnica detallada"),
    SUMMARY("Resumen", "Plantilla con resumen de métricas principales");

    private final String displayName;
    private final String description;

    ExportTemplate(String displayName, String description) {
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