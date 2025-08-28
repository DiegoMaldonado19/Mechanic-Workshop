package com.project.ayd.mechanic_workshop.features.inventory.enums;

public enum PurchaseOrderStatus {
    PENDIENTE("Pendiente", "Orden creada pero no enviada"),
    ENVIADA("Enviada", "Orden enviada al proveedor"),
    CONFIRMADA("Confirmada", "Proveedor confirmó la orden"),
    ENTREGADA("Entregada", "Orden entregada"),
    CANCELADA("Cancelada", "Orden cancelada");

    private final String displayName;
    private final String description;

    PurchaseOrderStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public static PurchaseOrderStatus fromDisplayName(String displayName) {
        for (PurchaseOrderStatus status : values()) {
            if (status.displayName.equals(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown purchase order status: " + displayName);
    }
}