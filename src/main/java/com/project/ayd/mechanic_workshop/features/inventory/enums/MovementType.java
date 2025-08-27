package com.project.ayd.mechanic_workshop.features.inventory.enums;

public enum MovementType {
    ENTRADA("Entrada", "Movimiento de entrada de inventario"),
    SALIDA("Salida", "Movimiento de salida de inventario"),
    AJUSTE("Ajuste", "Ajuste de inventario"),
    TRANSFERENCIA("Transferencia", "Transferencia entre ubicaciones"),
    DEVOLUCION("Devolución", "Devolución de producto"),
    MERMA("Merma", "Pérdida o deterioro de producto");

    private final String displayName;
    private final String description;

    MovementType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public static MovementType fromDisplayName(String displayName) {
        for (MovementType type : values()) {
            if (type.displayName.equals(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown movement type: " + displayName);
    }
}