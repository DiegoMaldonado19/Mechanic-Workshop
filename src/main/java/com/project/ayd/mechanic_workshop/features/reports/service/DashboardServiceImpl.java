package com.project.ayd.mechanic_workshop.features.reports.service;

import com.project.ayd.mechanic_workshop.features.reports.dto.DashboardResponse;
import com.project.ayd.mechanic_workshop.features.reports.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    private final ReportRepository reportRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse getDashboardData() {
        log.info("Generating dashboard data");

        // Métricas generales de trabajos
        Long totalActiveWorks = reportRepository.countActiveWorks();
        Long totalCompletedWorks = reportRepository.countCompletedWorks();
        Long totalPendingWorks = reportRepository.countPendingWorks();

        // Métricas financieras
        BigDecimal totalIncomeToday = reportRepository.getTotalIncomeToday();
        BigDecimal totalIncomeThisMonth = reportRepository.getTotalIncomeThisMonth();
        BigDecimal totalIncomeThisYear = reportRepository.getTotalIncomeThisYear();
        BigDecimal totalPendingPayments = reportRepository.getTotalPendingPayments();

        // Métricas de inventario
        Long totalPartsInStock = reportRepository.countPartsInStock();
        Long lowStockParts = reportRepository.countLowStockParts();
        Long outOfStockParts = reportRepository.countOutOfStockParts();

        // Métricas de empleados (calculadas a partir de trabajos activos)
        Long busyEmployees = totalActiveWorks; // Simplificación, cada trabajo activo tiene un empleado
        Long totalEmployees = 10L; // Esto debería venir de una query específica
        Long availableEmployees = totalEmployees - busyEmployees;

        // Datos para gráficos
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        List<DashboardResponse.ChartData> incomeByMonth = getIncomeChartData(sixMonthsAgo, LocalDateTime.now());
        List<DashboardResponse.ChartData> worksByStatus = getWorkStatusChartData();
        List<DashboardResponse.ChartData> worksByType = getWorkTypeChartData(sixMonthsAgo, LocalDateTime.now());

        // Top performers
        Map<String, BigDecimal> topMechanicsByRevenue = getTopMechanicsByRevenue();
        Map<String, Long> topUsedParts = getTopUsedParts();

        return DashboardResponse.builder()
                .totalActiveWorks(totalActiveWorks)
                .totalCompletedWorks(totalCompletedWorks)
                .totalPendingWorks(totalPendingWorks)
                .totalVehiclesInService(totalActiveWorks) // Simplificación
                .totalIncomeToday(totalIncomeToday)
                .totalIncomeThisMonth(totalIncomeThisMonth)
                .totalIncomeThisYear(totalIncomeThisYear)
                .totalPendingPayments(totalPendingPayments)
                .totalPartsInStock(totalPartsInStock)
                .lowStockParts(lowStockParts)
                .outOfStockParts(outOfStockParts)
                .totalEmployees(totalEmployees)
                .busyEmployees(busyEmployees)
                .availableEmployees(availableEmployees)
                .incomeByMonth(incomeByMonth)
                .worksByStatus(worksByStatus)
                .worksByType(worksByType)
                .topMechanicsByRevenue(topMechanicsByRevenue)
                .topUsedParts(topUsedParts)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse getDashboardDataForPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating dashboard data for period: {} to {}", startDate, endDate);

        // Similar al método anterior pero con filtros de fecha donde sea aplicable
        return getDashboardData(); // Simplificación por ahora
    }

    @Override
    public void refreshDashboardCache() {
        log.info("Refreshing dashboard cache");
        // Implementar cache refresh si se usa cache
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse.ChartData[] getIncomeChartData(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> rawData = reportRepository.getIncomeByMonth(startDate.toLocalDate(), endDate.toLocalDate());
        List<DashboardResponse.ChartData> chartData = new ArrayList<>();

        for (Object[] row : rawData) {
            chartData.add(DashboardResponse.ChartData.builder()
                    .label((String) row[0])
                    .value((BigDecimal) row[1])
                    .category("Ingresos")
                    .build());
        }

        return chartData.toArray(new DashboardResponse.ChartData[0]);
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse.ChartData[] getWorkStatusChartData() {
        LocalDateTime monthAgo = LocalDateTime.now().minusMonths(1);
        List<Object[]> rawData = reportRepository.getWorksByStatus(monthAgo, LocalDateTime.now());
        List<DashboardResponse.ChartData> chartData = new ArrayList<>();

        for (Object[] row : rawData) {
            chartData.add(DashboardResponse.ChartData.builder()
                    .label((String) row[0])
                    .value(BigDecimal.valueOf((Long) row[1]))
                    .category("Estado de Trabajos")
                    .build());
        }

        return chartData.toArray(new DashboardResponse.ChartData[0]);
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse.ChartData[] getWorkTypeChartData(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> rawData = reportRepository.getWorksByType(startDate, endDate);
        List<DashboardResponse.ChartData> chartData = new ArrayList<>();

        for (Object[] row : rawData) {
            chartData.add(DashboardResponse.ChartData.builder()
                    .label((String) row[0])
                    .value(BigDecimal.valueOf((Long) row[1]))
                    .category("Tipo de Servicio")
                    .build());
        }

        return chartData.toArray(new DashboardResponse.ChartData[0]);
    }

    private List<DashboardResponse.ChartData> getIncomeChartData(LocalDateTime startDate, LocalDateTime endDate) {
        DashboardResponse.ChartData[] data = getIncomeChartData(startDate, endDate);
        return List.of(data);
    }

    private List<DashboardResponse.ChartData> getWorkStatusChartData() {
        DashboardResponse.ChartData[] data = getWorkStatusChartData();
        return List.of(data);
    }

    private List<DashboardResponse.ChartData> getWorkTypeChartData(LocalDateTime startDate, LocalDateTime endDate) {
        DashboardResponse.ChartData[] data = getWorkTypeChartData(startDate, endDate);
        return List.of(data);
    }

    private Map<String, BigDecimal> getTopMechanicsByRevenue() {
        LocalDateTime monthAgo = LocalDateTime.now().minusMonths(1);
        List<Object[]> rawData = reportRepository.getEmployeePerformance(monthAgo, LocalDateTime.now());
        Map<String, BigDecimal> topMechanics = new HashMap<>();

        for (Object[] row : rawData) {
            if (topMechanics.size() >= 5)
                break; // Top 5
            String employeeName = (String) row[0];
            BigDecimal totalRevenue = (BigDecimal) row[5];
            topMechanics.put(employeeName, totalRevenue);
        }

        return topMechanics;
    }

    private Map<String, Long> getTopUsedParts() {
        LocalDateTime monthAgo = LocalDateTime.now().minusMonths(1);
        List<Object[]> rawData = reportRepository.getPartUsageStatistics(monthAgo, LocalDateTime.now());
        Map<String, Long> topParts = new HashMap<>();

        for (Object[] row : rawData) {
            if (topParts.size() >= 5)
                break; // Top 5
            String partName = (String) row[0];
            Long totalQuantity = ((Number) row[2]).longValue();
            topParts.put(partName, totalQuantity);
        }

        return topParts;
    }
}