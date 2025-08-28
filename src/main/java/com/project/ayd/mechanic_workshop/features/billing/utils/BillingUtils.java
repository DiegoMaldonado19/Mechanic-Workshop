package com.project.ayd.mechanic_workshop.features.billing.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BillingUtils {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.12"); // IVA 12%
    private static final int DECIMAL_SCALE = 2;

    private BillingUtils() {
        // Utility class
    }

    /**
     * Calcula el monto de impuestos basado en el subtotal
     */
    public static BigDecimal calculateTaxAmount(BigDecimal subtotal) {
        if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return subtotal.multiply(TAX_RATE).setScale(DECIMAL_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Calcula el total incluyendo impuestos
     */
    public static BigDecimal calculateTotalAmount(BigDecimal subtotal, BigDecimal taxAmount) {
        BigDecimal validSubtotal = subtotal != null ? subtotal : BigDecimal.ZERO;
        BigDecimal validTaxAmount = taxAmount != null ? taxAmount : BigDecimal.ZERO;
        return validSubtotal.add(validTaxAmount).setScale(DECIMAL_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Calcula el porcentaje de pago completado
     */
    public static BigDecimal calculatePaymentPercentage(BigDecimal amountPaid, BigDecimal totalAmount) {
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        if (amountPaid == null) {
            return BigDecimal.ZERO;
        }
        return amountPaid.divide(totalAmount, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(DECIMAL_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Calcula el saldo pendiente
     */
    public static BigDecimal calculatePendingAmount(BigDecimal totalAmount, BigDecimal amountPaid) {
        BigDecimal validTotal = totalAmount != null ? totalAmount : BigDecimal.ZERO;
        BigDecimal validPaid = amountPaid != null ? amountPaid : BigDecimal.ZERO;
        BigDecimal pending = validTotal.subtract(validPaid);
        return pending.max(BigDecimal.ZERO).setScale(DECIMAL_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Verifica si una factura está vencida
     */
    public static boolean isInvoiceOverdue(LocalDate dueDate) {
        if (dueDate == null) {
            return false;
        }
        return dueDate.isBefore(LocalDate.now());
    }

    /**
     * Calcula los días de retraso
     */
    public static long calculateOverdueDays(LocalDate dueDate) {
        if (dueDate == null || !isInvoiceOverdue(dueDate)) {
            return 0;
        }
        return ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }

    /**
     * Verifica si una cotización está vigente
     */
    public static boolean isQuotationValid(LocalDate validUntil) {
        if (validUntil == null) {
            return true; // Si no tiene fecha de vencimiento, es válida
        }
        return !validUntil.isBefore(LocalDate.now());
    }

    /**
     * Calcula la fecha de vencimiento por defecto (30 días desde hoy)
     */
    public static LocalDate calculateDefaultDueDate() {
        return LocalDate.now().plusDays(30);
    }

    /**
     * Calcula la fecha de validez por defecto para cotizaciones (30 días desde hoy)
     */
    public static LocalDate calculateDefaultValidUntil() {
        return LocalDate.now().plusDays(30);
    }

    /**
     * Formatea un monto monetario para visualización
     */
    public static String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "Q 0.00";
        }
        return String.format("Q %.2f", amount);
    }

    /**
     * Redondea un monto a 2 decimales
     */
    public static BigDecimal roundAmount(BigDecimal amount) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        return amount.setScale(DECIMAL_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Valida que un monto sea positivo
     */
    public static boolean isValidAmount(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Obtiene la tasa de impuesto actual
     */
    public static BigDecimal getTaxRate() {
        return TAX_RATE;
    }

    /**
     * Calcula los días hasta el vencimiento
     */
    public static long calculateDaysUntilDue(LocalDate dueDate) {
        if (dueDate == null) {
            return Long.MAX_VALUE;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }
}