package com.project.ayd.mechanic_workshop.features.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingSummaryResponse {

    private Long totalQuotations;
    private Long approvedQuotations;
    private BigDecimal totalQuotationsAmount;
    private BigDecimal approvedQuotationsAmount;

    private Long totalInvoices;
    private BigDecimal totalInvoicesAmount;
    private BigDecimal paidAmount;
    private BigDecimal pendingAmount;
    private BigDecimal overdueAmount;

    private Long totalPayments;
    private BigDecimal totalPaymentsAmount;

    private Double quotationApprovalRate;
    private Double invoicePaymentRate;
}