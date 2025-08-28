package com.project.ayd.mechanic_workshop.features.reports.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardResponse {
    // Métricas generales
    private Long totalActiveWorks;
    private Long totalCompletedWorks;
    private Long totalPendingWorks;
    private Long totalVehiclesInService;

    // Métricas financieras
    private BigDecimal totalIncomeToday;
    private BigDecimal totalIncomeThisMonth;
    private BigDecimal totalIncomeThisYear;
    private BigDecimal totalPendingPayments;

    // Métricas de inventario
    private Long totalPartsInStock;
    private Long lowStockParts;
    private Long outOfStockParts;

    // Métricas de empleados
    private Long totalEmployees;
    private Long busyEmployees;
    private Long availableEmployees;

    // Gráficos y tendencias
    private List<ChartData> incomeByMonth;
    private List<ChartData> worksByStatus;
    private List<ChartData> worksByType;
    private Map<String, BigDecimal> topMechanicsByRevenue;
    private Map<String, Long> topUsedParts;

    @Data
    @Builder
    public static class ChartData {
        private String label;
        private BigDecimal value;
        private LocalDate date;
        private String category;
    }
}