package com.project.ayd.mechanic_workshop.features.billing.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentMethodEnum {

    EFECTIVO("Efectivo", "Pago en efectivo"),
    TARJETA_CREDITO("Tarjeta de crédito", "Pago con tarjeta de crédito"),
    TARJETA_DEBITO("Tarjeta de débito", "Pago con tarjeta de débito"),
    TRANSFERENCIA_BANCARIA("Transferencia bancaria", "Transferencia bancaria"),
    CHEQUE("Cheque", "Pago con cheque"),
    DEPOSITO_BANCARIO("Depósito bancario", "Depósito directo en cuenta"),
    PAGO_MOVIL("Pago móvil", "Pago a través de aplicación móvil"),
    CRYPTOCURRENCY("Cryptocurrency", "Pago con criptomonedas");

    private final String name;
    private final String description;

    public static PaymentMethodEnum fromName(String name) {
        for (PaymentMethodEnum method : values()) {
            if (method.getName().equalsIgnoreCase(name)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Método de pago no válido: " + name);
    }

    public boolean isElectronic() {
        return this == TARJETA_CREDITO || this == TARJETA_DEBITO ||
                this == TRANSFERENCIA_BANCARIA || this == PAGO_MOVIL ||
                this == CRYPTOCURRENCY;
    }

    public boolean requiresReference() {
        return this == TARJETA_CREDITO || this == TARJETA_DEBITO ||
                this == TRANSFERENCIA_BANCARIA || this == CHEQUE ||
                this == DEPOSITO_BANCARIO || this == CRYPTOCURRENCY;
    }
}