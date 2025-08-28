package com.project.ayd.mechanic_workshop.features.reports.service;

import com.project.ayd.mechanic_workshop.features.reports.dto.DashboardResponse;

import java.time.LocalDateTime;

public interface DashboardService {

    DashboardResponse getDashboardData();

    DashboardResponse getDashboardDataForPeriod(LocalDateTime startDate, LocalDateTime endDate);

    void refreshDashboardCache();

    DashboardResponse.ChartData[] getIncomeChartData(LocalDateTime startDate, LocalDateTime endDate);

    DashboardResponse.ChartData[] getWorkStatusChartData();

    DashboardResponse.ChartData[] getWorkTypeChartData(LocalDateTime startDate, LocalDateTime endDate);
}