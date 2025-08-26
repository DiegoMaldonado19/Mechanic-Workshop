package com.project.ayd.mechanic_workshop.features.workorders.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MaintenanceType {

    CORRECTIVE("Correctivo", "Mantenimiento por fallas o averías detectadas"),
    PREVENTIVE("Preventivo", "Mantenimiento programado para prevenir fallas"),
    PREDICTIVE("Predictivo", "Mantenimiento basado en condición y análisis"),
    EMERGENCY("Emergencia", "Mantenimiento de emergencia por falla crítica");

    private final String displayName;
    private final String description;
}