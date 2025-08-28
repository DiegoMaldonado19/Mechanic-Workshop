package com.project.ayd.mechanic_workshop.features.reports.service;

import com.project.ayd.mechanic_workshop.features.reports.dto.DashboardResponse;
import com.project.ayd.mechanic_workshop.features.reports.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

        try {
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

            // Métricas de empleados
            Long totalEmployees = reportRepository.countTotalEmployees();
            Long busyEmployees = totalActiveWorks; // Simplificación
            Long availableEmployees = Math.max(0L, totalEmployees - busyEmployees);

            // Datos para gráficos
            LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
            List<DashboardResponse.ChartData> incomeByMonth = getIncomeChartDataList(sixMonthsAgo, LocalDateTime.now());
            List<DashboardResponse.ChartData> worksByStatus = getWorkStatusChartDataList();
            List<DashboardResponse.ChartData> worksByType = getWorkTypeChartDataList(sixMonthsAgo, LocalDateTime.now());

            // Top performers
            Map<String, BigDecimal> topMechanicsByRevenue = getTopMechanicsByRevenue();
            Map<String, Long> topUsedParts = getTopUsedParts();

            return DashboardResponse.builder()
                    .totalActiveWorks(totalActiveWorks != null ? totalActiveWorks : 0L)
                    .totalCompletedWorks(totalCompletedWorks != null ? totalCompletedWorks : 0L)
                    .totalPendingWorks(totalPendingWorks != null ? totalPendingWorks : 0L)
                    .totalVehiclesInService(totalActiveWorks != null ? totalActiveWorks : 0L) // Simplificación
                    .totalIncomeToday(totalIncomeToday != null ? totalIncomeToday : BigDecimal.ZERO)
                    .totalIncomeThisMonth(totalIncomeThisMonth != null ? totalIncomeThisMonth : BigDecimal.ZERO)
                    .totalIncomeThisYear(totalIncomeThisYear != null ? totalIncomeThisYear : BigDecimal.ZERO)
                    .totalPendingPayments(totalPendingPayments != null ? totalPendingPayments : BigDecimal.ZERO)
                    .totalPartsInStock(totalPartsInStock != null ? totalPartsInStock : 0L)
                    .lowStockParts(lowStockParts != null ? lowStockParts : 0L)
                    .outOfStockParts(outOfStockParts != null ? outOfStockParts : 0L)
                    .totalEmployees(totalEmployees != null ? totalEmployees : 0L)
                    .busyEmployees(busyEmployees != null ? busyEmployees : 0L)
                    .availableEmployees(availableEmployees)
                    .incomeByMonth(incomeByMonth)
                    .worksByStatus(worksByStatus)
                    .worksByType(worksByType)
                    .topMechanicsByRevenue(topMechanicsByRevenue)
                    .topUsedParts(topUsedParts)
                    .build();

        } catch (Exception e) {
            log.error("Error generating dashboard data", e);
            // Devolver dashboard con valores por defecto en caso de error
            return getDefaultDashboardResponse();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse getDashboardDataForPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating dashboard data for period: {} to {}", startDate, endDate);

        try {
            // Para este método podríamos implementar filtros específicos por fecha
            // Por simplicidad, devolvemos el dashboard general
            return getDashboardData();
        } catch (Exception e) {
            log.error("Error generating dashboard data for period", e);
            return getDefaultDashboardResponse();
        }
    }

    @Override
    public void refreshDashboardCache() {
        log.info("Refreshing dashboard cache");
        // Implementar cache refresh si se usa cache en el futuro
        // Por ahora solo logueamos la acción
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse.ChartData[] getIncomeChartData(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            List<Object[]> rawData = reportRepository.getIncomeByMonth(startDate.toLocalDate(), endDate.toLocalDate());
            List<DashboardResponse.ChartData> chartData = new ArrayList<>();

            for (Object[] row : rawData) {
                if (row.length >= 2 && row[0] != null && row[1] != null) {
                    chartData.add(DashboardResponse.ChartData.builder()
                            .label((String) row[0])
                            .value(new BigDecimal(row[1].toString()))
                            .category("Ingresos")
                            .build());
                }
            }

            return chartData.toArray(new DashboardResponse.ChartData[0]);
        } catch (Exception e) {
            log.error("Error getting income chart data", e);
            return new DashboardResponse.ChartData[0];
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse.ChartData[] getWorkStatusChartData() {
        try {
            LocalDateTime monthAgo = LocalDateTime.now().minusMonths(1);
            List<Object[]> rawData = reportRepository.getWorksByStatus(monthAgo, LocalDateTime.now());
            List<DashboardResponse.ChartData> chartData = new ArrayList<>();

            for (Object[] row : rawData) {
                if (row.length >= 2 && row[0] != null && row[1] != null) {
                    chartData.add(DashboardResponse.ChartData.builder()
                            .label((String) row[0])
                            .value(BigDecimal.valueOf(((Number) row[1]).longValue()))
                            .category("Estado de Trabajos")
                            .build());
                }
            }

            return chartData.toArray(new DashboardResponse.ChartData[0]);
        } catch (Exception e) {
            log.error("Error getting work status chart data", e);
            return new DashboardResponse.ChartData[0];
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse.ChartData[] getWorkTypeChartData(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            List<Object[]> rawData = reportRepository.getWorksByType(startDate, endDate);
            List<DashboardResponse.ChartData> chartData = new ArrayList<>();

            for (Object[] row : rawData) {
                if (row.length >= 2 && row[0] != null && row[1] != null) {
                    chartData.add(DashboardResponse.ChartData.builder()
                            .label((String) row[0])
                            .value(BigDecimal.valueOf(((Number) row[1]).longValue()))
                            .category("Tipo de Servicio")
                            .build());
                }
            }

            return chartData.toArray(new DashboardResponse.ChartData[0]);
        } catch (Exception e) {
            log.error("Error getting work type chart data", e);
            return new DashboardResponse.ChartData[0];
        }
    }

    // Helper methods
    private List<DashboardResponse.ChartData> getIncomeChartDataList(LocalDateTime startDate, LocalDateTime endDate) {
        DashboardResponse.ChartData[] data = getIncomeChartData(startDate, endDate);
        return List.of(data);
    }

    private List<DashboardResponse.ChartData> getWorkStatusChartDataList() {
        DashboardResponse.ChartData[] data = getWorkStatusChartData();
        return List.of(data);
    }

    private List<DashboardResponse.ChartData> getWorkTypeChartDataList(LocalDateTime startDate, LocalDateTime endDate) {
        DashboardResponse.ChartData[] data = getWorkTypeChartData(startDate, endDate);
        return List.of(data);
    }

    private Map<String, BigDecimal> getTopMechanicsByRevenue() {
        try {
            LocalDateTime monthAgo = LocalDateTime.now().minusMonths(1);
            List<Object[]> rawData = reportRepository.getEmployeePerformance(monthAgo, LocalDateTime.now());
            Map<String, BigDecimal> topMechanics = new HashMap<>();

            int count = 0;
            for (Object[] row : rawData) {
                if (count >= 5)
                    break; // Top 5
                if (row.length >= 6 && row[0] != null && row[5] != null) {
                    String employeeName = (String) row[0];
                    BigDecimal totalRevenue = new BigDecimal(row[5].toString());
                    topMechanics.put(employeeName, totalRevenue);
                    count++;
                }
            }

            return topMechanics;
        } catch (Exception e) {
            log.error("Error getting top mechanics by revenue", e);
            return new HashMap<>();
        }
    }

    private Map<String, Long> getTopUsedParts() {
        try {
            LocalDateTime monthAgo = LocalDateTime.now().minusMonths(1);
            List<Object[]> rawData = reportRepository.getPartUsageStatistics(monthAgo, LocalDateTime.now());
            Map<String, Long> topParts = new HashMap<>();

            int count = 0;
            for (Object[] row : rawData) {
                if (count >= 5)
                    break; // Top 5
                if (row.length >= 3 && row[0] != null && row[2] != null) {
                    String partName = (String) row[0];
                    Long totalQuantity = ((Number) row[2]).longValue();
                    topParts.put(partName, totalQuantity);
                    count++;
                }
            }

            return topParts;
        } catch (Exception e) {
            log.error("Error getting top used parts", e);
            return new HashMap<>();
        }
    }

    private DashboardResponse getDefaultDashboardResponse() {
        return DashboardResponse.builder()
                .totalActiveWorks(0L)
                .totalCompletedWorks(0L)
                .totalPendingWorks(0L)
                .totalVehiclesInService(0L)
                .totalIncomeToday(BigDecimal.ZERO)
                .totalIncomeThisMonth(BigDecimal.ZERO)
                .totalIncomeThisYear(BigDecimal.ZERO)
                .totalPendingPayments(BigDecimal.ZERO)
                .totalPartsInStock(0L)
                .lowStockParts(0L)
                .outOfStockParts(0L)
                .totalEmployees(0L)
                .busyEmployees(0L)
                .availableEmployees(0L)
                .incomeByMonth(new ArrayList<>())
                .worksByStatus(new ArrayList<>())
                .worksByType(new ArrayList<>())
                .topMechanicsByRevenue(new HashMap<>())
                .topUsedParts(new HashMap<>())
                .build();
    }
}