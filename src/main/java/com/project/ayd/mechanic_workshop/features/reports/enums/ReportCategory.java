package com.project.ayd.mechanic_workshop.features.reports.enums;

public enum ReportCategory {
    FINANCIAL("Financiero", "Reportes relacionados con ingresos, egresos y análisis financiero"),
    OPERATIONAL("Operacional", "Reportes de trabajos, mantenimientos y operaciones del taller"),
    INVENTORY("Inventario", "Reportes de repuestos, stock y movimientos de inventario"),
    CLIENT("Clientes", "Reportes de historial y satisfacción de clientes"),
    EMPLOYEE("Empleados", "Reportes de desempeño y productividad de empleados"),
    MAINTENANCE("Mantenimiento", "Reportes específicos de mantenimientos preventivos y correctivos"),
    ANALYTICS("Analítica", "Reportes de tendencias, patrones y análisis avanzado");

    private final String displayName;
    private final String description;

    ReportCategory(String displayName, String description) {
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