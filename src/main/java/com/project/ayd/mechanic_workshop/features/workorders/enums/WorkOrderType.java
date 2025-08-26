package com.project.ayd.mechanic_workshop.features.workorders.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WorkOrderType {

    CORRECTIVE(1L, "Correctivo", "Trabajo de reparación por fallas detectadas"),
    PREVENTIVE(2L, "Preventivo", "Mantenimiento programado y prevención");

    private final Long id;
    private final String displayName;
    private final String description;

    public static WorkOrderType fromId(Long id) {
        for (WorkOrderType type : values()) {
            if (type.getId().equals(id)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid WorkOrderType id: " + id);
    }
}