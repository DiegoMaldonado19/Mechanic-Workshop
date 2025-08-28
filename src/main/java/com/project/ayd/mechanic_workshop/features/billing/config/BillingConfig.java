package com.project.ayd.mechanic_workshop.features.billing.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Configuration
@ConfigurationProperties(prefix = "billing")
@Validated
@Getter
@Setter
public class BillingConfig {

    /**
     * Tasa de impuesto por defecto (IVA)
     */
    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal defaultTaxRate = new BigDecimal("0.12");

    /**
     * Días por defecto para vencimiento de facturas
     */
    @NotNull
    @Min(1)
    private Integer defaultInvoiceDueDays = 30;

    /**
     * Días por defecto para validez de cotizaciones
     */
    @NotNull
    @Min(1)
    private Integer defaultQuotationValidDays = 30;

    /**
     * Días de gracia antes de marcar como vencido
     */
    @NotNull
    @Min(0)
    private Integer graceDays = 3;

    /**
     * Monto mínimo de pago permitido
     */
    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal minimumPaymentAmount = new BigDecimal("0.01");

    /**
     * Prefijo para números de factura
     */
    @NotNull
    private String invoiceNumberPrefix = "INV-";

    /**
     * Prefijo para números de cotización
     */
    @NotNull
    private String quotationNumberPrefix = "COT-";

    /**
     * Habilitar notificaciones automáticas de vencimiento
     */
    private boolean enableOverdueNotifications = true;

    /**
     * Días antes del vencimiento para enviar recordatorio
     */
    @Min(1)
    private Integer reminderDaysBeforeDue = 7;

    /**
     * Permitir pagos parciales
     */
    private boolean allowPartialPayments = true;

    /**
     * Aplicar impuestos automáticamente
     */
    private boolean autoApplyTax = true;
}