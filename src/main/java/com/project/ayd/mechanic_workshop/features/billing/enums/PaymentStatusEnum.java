package com.project.ayd.mechanic_workshop.features.billing.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatusEnum {

    PENDIENTE("Pendiente", "Pago no recibido aún"),
    PARCIAL("Parcial", "Pago parcial recibido"),
    PAGADO("Pagado", "Pago completo recibido"),
    VENCIDO("Vencido", "Pago vencido");

    private final String name;
    private final String description;

    public static PaymentStatusEnum fromName(String name) {
        for (PaymentStatusEnum status : values()) {
            if (status.getName().equalsIgnoreCase(name)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Estado de pago no válido: " + name);
    }

    public boolean isPaid() {
        return this == PAGADO;
    }

    public boolean isPending() {
        return this == PENDIENTE;
    }

    public boolean isPartial() {
        return this == PARCIAL;
    }

    public boolean isOverdue() {
        return this == VENCIDO;
    }
}