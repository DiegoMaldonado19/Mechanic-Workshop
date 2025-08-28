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
public class QuotationRequest {

    @NotNull(message = "El ID del trabajo es requerido")
    @Positive(message = "El ID del trabajo debe ser un número positivo")
    private Long workId;

    @NotNull(message = "El costo total de repuestos es requerido")
    @DecimalMin(value = "0.0", inclusive = true, message = "El costo de repuestos no puede ser negativo")
    private BigDecimal totalPartsCost;

    @NotNull(message = "El costo total de mano de obra es requerido")
    @DecimalMin(value = "0.0", inclusive = true, message = "El costo de mano de obra no puede ser negativo")
    private BigDecimal totalLaborCost;

    @FutureOrPresent(message = "La fecha de validez debe ser presente o futura")
    private LocalDate validUntil;
}