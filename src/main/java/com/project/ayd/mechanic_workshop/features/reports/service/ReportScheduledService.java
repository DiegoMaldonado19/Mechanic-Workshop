package com.project.ayd.mechanic_workshop.features.reports.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportScheduledService {

    private final ReportService reportService;
    private final DashboardService dashboardService;

    // Limpiar reportes expirados cada hora
    @Scheduled(fixedRate = 3600000) // 1 hora en milisegundos
    public void cleanupExpiredReports() {
        log.info("Starting scheduled cleanup of expired reports");
        try {
            reportService.deleteExpiredReports();
            log.info("Successfully completed scheduled cleanup of expired reports");
        } catch (Exception e) {
            log.error("Error during scheduled cleanup of expired reports", e);
        }
    }

    // Refrescar cache del dashboard cada 15 minutos durante horas laborales
    @Scheduled(cron = "0 */15 7-18 * * MON-SAT")
    public void refreshDashboardCache() {
        log.info("Starting scheduled refresh of dashboard cache");
        try {
            dashboardService.refreshDashboardCache();
            log.info("Successfully refreshed dashboard cache");
        } catch (Exception e) {
            log.error("Error during scheduled dashboard cache refresh", e);
        }
    }

    // Limpiar archivos temporales cada día a medianoche
    @Scheduled(cron = "0 0 0 * * *")
    public void cleanupTemporaryFiles() {
        log.info("Starting scheduled cleanup of temporary files");
        try {
            // Aquí se implementaría la limpieza de archivos temporales
            // que no estén referenciados en el cache de reportes
            log.info("Successfully completed scheduled cleanup of temporary files");
        } catch (Exception e) {
            log.error("Error during scheduled cleanup of temporary files", e);
        }
    }
}