package com.project.ayd.mechanic_workshop.features.reports.enums;

public enum ChartType {
    BAR("Barras", "Gráfico de barras verticales"),
    HORIZONTAL_BAR("Barras Horizontales", "Gráfico de barras horizontales"),
    LINE("Líneas", "Gráfico de líneas"),
    PIE("Circular", "Gráfico circular o de pastel"),
    DONUT("Dona", "Gráfico de dona"),
    AREA("Área", "Gráfico de área"),
    SCATTER("Dispersión", "Gráfico de dispersión"),
    COMBO("Combinado", "Gráfico combinado con múltiples tipos");

    private final String displayName;
    private final String description;

    ChartType(String displayName, String description) {
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