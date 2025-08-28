package com.project.ayd.mechanic_workshop.features.billing.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceRequest {

    @NotNull(message = "El ID del trabajo es requerido")
    @Positive(message = "El ID del trabajo debe ser un número positivo")
    private Long workId;

    @Positive(message = "El ID de la cotización debe ser un número positivo")
    private Long quotationId;

    @NotNull(message = "El subtotal es requerido")
    @DecimalMin(value = "0.01", message = "El subtotal debe ser mayor a cero")
    private BigDecimal subtotal;

    @DecimalMin(value = "0.0", inclusive = true, message = "El monto de impuestos no puede ser negativo")
    private BigDecimal taxAmount;

    @FutureOrPresent(message = "La fecha de vencimiento debe ser presente o futura")
    private LocalDate dueDate;
}