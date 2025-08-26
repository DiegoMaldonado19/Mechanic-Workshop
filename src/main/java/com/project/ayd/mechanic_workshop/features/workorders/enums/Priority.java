package com.project.ayd.mechanic_workshop.features.workorders.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Priority {

    LOW(1, "Baja"),
    MEDIUM(2, "Media"),
    HIGH(3, "Alta"),
    URGENT(4, "Urgente"),
    CRITICAL(5, "Crítica");

    private final Integer level;
    private final String displayName;

    public static Priority fromLevel(Integer level) {
        for (Priority priority : values()) {
            if (priority.getLevel().equals(level)) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Invalid Priority level: " + level);
    }
}