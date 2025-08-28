package com.project.ayd.mechanic_workshop.features.reports.validator;

import com.project.ayd.mechanic_workshop.features.reports.dto.ReportRequest;
import com.project.ayd.mechanic_workshop.features.reports.exception.ReportDataException;
import com.project.ayd.mechanic_workshop.features.reports.exception.ReportFormatException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class ReportValidator {

    private static final int MAX_DATE_RANGE_YEARS = 2;
    private static final int MAX_FUTURE_DAYS = 1;

    public void validateReportRequest(ReportRequest request) {
        if (request == null) {
            throw new ReportDataException("Report request cannot be null");
        }

        if (request.getReportType() == null) {
            throw new ReportDataException("Report type is required");
        }

        if (request.getFormat() == null) {
            throw new ReportFormatException("Report format is required");
        }

        validateDateRange(request);
    }

    private void validateDateRange(ReportRequest request) {
        LocalDateTime startDate = request.getStartDate();
        LocalDateTime endDate = request.getEndDate();

        // Si se especifica período, usar esas fechas
        if (request.getPeriod() != null) {
            startDate = request.getPeriod().getStartDate();
            endDate = request.getPeriod().getEndDate();
        }

        if (startDate != null && endDate != null) {
            // Validar que la fecha de inicio no sea posterior a la fecha de fin
            if (startDate.isAfter(endDate)) {
                throw new ReportDataException("Start date cannot be after end date");
            }

            // Validar que el rango no sea demasiado amplio
            long yearsBetween = ChronoUnit.YEARS.between(startDate, endDate);
            if (yearsBetween > MAX_DATE_RANGE_YEARS) {
                throw new ReportDataException("Date range cannot exceed " + MAX_DATE_RANGE_YEARS + " years");
            }

            // Validar que las fechas no sean muy futuras
            LocalDateTime maxFutureDate = LocalDateTime.now().plusDays(MAX_FUTURE_DAYS);
            if (startDate.isAfter(maxFutureDate) || endDate.isAfter(maxFutureDate)) {
                throw new ReportDataException("Dates cannot be more than " + MAX_FUTURE_DAYS + " day(s) in the future");
            }
        }
    }

    public void validateReportId(String reportId) {
        if (reportId == null || reportId.trim().isEmpty()) {
            throw new ReportDataException("Report ID is required");
        }

        if (reportId.length() > 100) {
            throw new ReportDataException("Report ID is too long");
        }

        // Validar formato del ID (debe contener solo caracteres alfanuméricos y guiones
        // bajos)
        if (!reportId.matches("^[a-zA-Z0-9_]+$")) {
            throw new ReportDataException("Invalid report ID format");
        }
    }

    public void validateFilePath(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new ReportDataException("File path is required");
        }

        // Validaciones básicas de seguridad para evitar path traversal
        if (filePath.contains("..") || filePath.contains("//")) {
            throw new ReportDataException("Invalid file path");
        }
    }
}