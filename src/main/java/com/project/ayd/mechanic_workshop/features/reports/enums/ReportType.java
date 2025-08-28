package com.project.ayd.mechanic_workshop.features.reports.enums;

public enum ReportType {
    FINANCIAL_INCOME("Reporte de Ingresos"),
    FINANCIAL_EXPENSES("Reporte de Egresos"),
    WORK_BY_DATE("Trabajos por Fecha"),
    WORK_BY_TYPE("Trabajos por Tipo de Servicio"),
    WORK_BY_EMPLOYEE("Trabajos por Empleado"),
    PARTS_USAGE("Uso de Repuestos"),
    PARTS_BY_BRAND("Repuestos por Marca de Vehículo"),
    CLIENT_HISTORY("Historial de Clientes"),
    VEHICLE_BEHAVIOR("Comportamiento de Vehículos"),
    PREVENTIVE_MAINTENANCE("Mantenimientos Preventivos"),
    CORRECTIVE_MAINTENANCE("Mantenimientos Correctivos"),
    PAYMENT_STATUS("Estado de Pagos"),
    INVENTORY_STOCK("Estado de Inventario"),
    DASHBOARD_SUMMARY("Resumen Dashboard");

    private final String displayName;

    ReportType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}