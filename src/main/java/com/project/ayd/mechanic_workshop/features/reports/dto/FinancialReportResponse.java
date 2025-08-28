package com.project.ayd.mechanic_workshop.features.reports.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class FinancialReportResponse {
    private String reportPeriod;
    private LocalDate startDate;
    private LocalDate endDate;

    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netProfit;
    private BigDecimal profitMargin;

    private List<IncomeDetail> incomeBySource;
    private List<ExpenseDetail> expensesByCategory;
    private List<MonthlyFinancial> monthlyBreakdown;

    @Data
    @Builder
    public static class IncomeDetail {
        private String source;
        private BigDecimal amount;
        private BigDecimal percentage;
        private Long transactionCount;
    }

    @Data
    @Builder
    public static class ExpenseDetail {
        private String category;
        private BigDecimal amount;
        private BigDecimal percentage;
        private Long transactionCount;
    }

    @Data
    @Builder
    public static class MonthlyFinancial {
        private String month;
        private BigDecimal income;
        private BigDecimal expenses;
        private BigDecimal profit;
    }
}