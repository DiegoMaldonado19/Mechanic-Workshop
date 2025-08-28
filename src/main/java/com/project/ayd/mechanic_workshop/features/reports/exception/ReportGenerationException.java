package com.project.ayd.mechanic_workshop.features.reports.exception;

public class ReportGenerationException extends ReportException {

    public ReportGenerationException(String message) {
        super(message, "REPORT_GENERATION_ERROR");
    }

    public ReportGenerationException(String message, Throwable cause) {
        super(message, "REPORT_GENERATION_ERROR", cause);
    }
}