package com.project.ayd.mechanic_workshop.features.inventory.enums;

public enum ReferenceType {
    ORDEN_COMPRA("Orden de compra", "Referencia a orden de compra"),
    TRABAJO("Trabajo", "Referencia a orden de trabajo"),
    AJUSTE("Ajuste", "Referencia a ajuste manual"),
    TRANSFERENCIA("Transferencia", "Referencia a transferencia"),
    DEVOLUCION("Devolución", "Referencia a devolución"),
    INVENTARIO_INICIAL("Inventario inicial", "Referencia a inventario inicial");

    private final String displayName;
    private final String description;

    ReferenceType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public static ReferenceType fromDisplayName(String displayName) {
        for (ReferenceType type : values()) {
            if (type.displayName.equals(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown reference type: " + displayName);
    }
}