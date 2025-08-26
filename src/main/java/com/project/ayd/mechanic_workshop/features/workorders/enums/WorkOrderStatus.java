package com.project.ayd.mechanic_workshop.features.workorders.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WorkOrderStatus {

    PENDING(1L, "Pendiente"),
    ASSIGNED(2L, "Asignado"),
    IN_PROGRESS(3L, "En progreso"),
    COMPLETED(4L, "Completado"),
    CANCELLED(5L, "Cancelado"),
    FINISHED_WITHOUT_EXECUTION(6L, "Finalizado sin ejecución");

    private final Long id;
    private final String displayName;

    public static WorkOrderStatus fromId(Long id) {
        for (WorkOrderStatus status : values()) {
            if (status.getId().equals(id)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid WorkOrderStatus id: " + id);
    }
}