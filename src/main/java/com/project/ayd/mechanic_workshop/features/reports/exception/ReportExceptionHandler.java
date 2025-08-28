package com.project.ayd.mechanic_workshop.features.reports.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.project.ayd.mechanic_workshop.features.reports")
@Slf4j
public class ReportExceptionHandler {

    @ExceptionHandler(ReportNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleReportNotFound(ReportNotFoundException ex) {
        log.warn("Report not found: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Report Not Found");
        response.put("message", ex.getMessage());
        response.put("errorCode", ex.getErrorCode());
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.NOT_FOUND.value());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ReportGenerationException.class)
    public ResponseEntity<Map<String, Object>> handleReportGeneration(ReportGenerationException ex) {
        log.error("Report generation failed: {}", ex.getMessage(), ex);

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Report Generation Failed");
        response.put("message", "Failed to generate the requested report. Please try again later.");
        response.put("errorCode", ex.getErrorCode());
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ReportFormatException.class)
    public ResponseEntity<Map<String, Object>> handleReportFormat(ReportFormatException ex) {
        log.warn("Invalid report format: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Invalid Format");
        response.put("message", ex.getMessage());
        response.put("errorCode", ex.getErrorCode());
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ReportDataException.class)
    public ResponseEntity<Map<String, Object>> handleReportData(ReportDataException ex) {
        log.error("Report data error: {}", ex.getMessage(), ex);

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Data Error");
        response.put("message", "Error processing report data: " + ex.getMessage());
        response.put("errorCode", ex.getErrorCode());
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ReportException.class)
    public ResponseEntity<Map<String, Object>> handleGeneralReport(ReportException ex) {
        log.error("General report error: {}", ex.getMessage(), ex);

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Report Error");
        response.put("message", ex.getMessage());
        response.put("errorCode", ex.getErrorCode());
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Invalid argument in reports: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Invalid Request");
        response.put("message", ex.getMessage());
        response.put("errorCode", "INVALID_ARGUMENT");
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        log.error("Unexpected error in reports module: {}", ex.getMessage(), ex);

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Internal Server Error");
        response.put("message", "An unexpected error occurred while processing the report request");
        response.put("errorCode", "INTERNAL_ERROR");
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}