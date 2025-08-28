package com.project.ayd.mechanic_workshop.features.inventory.enums;

public enum AlertLevel {
    LOW("Bajo", "Stock bajo"),
    CRITICAL("Crítico", "Stock crítico"),
    OUT_OF_STOCK("Sin stock", "Sin existencias");

    private final String displayName;
    private final String description;

    AlertLevel(String displayName, String description) {
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