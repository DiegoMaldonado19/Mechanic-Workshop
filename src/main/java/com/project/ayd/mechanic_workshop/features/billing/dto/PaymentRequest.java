package com.project.ayd.mechanic_workshop.features.billing.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    @NotNull(message = "El ID de la factura es requerido")
    @Positive(message = "El ID de la factura debe ser un número positivo")
    private Long invoiceId;

    @NotNull(message = "El monto del pago es requerido")
    @DecimalMin(value = "0.01", message = "El monto del pago debe ser mayor a cero")
    private BigDecimal amount;

    @NotNull(message = "El ID del método de pago es requerido")
    @Positive(message = "El ID del método de pago debe ser un número positivo")
    private Long paymentMethodId;

    private LocalDateTime paymentDate;

    @Size(max = 100, message = "El número de referencia no puede exceder los 100 caracteres")
    private String referenceNumber;

    @Size(max = 1000, message = "Las notas no pueden exceder los 1000 caracteres")
    private String notes;
}