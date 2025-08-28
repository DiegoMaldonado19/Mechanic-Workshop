package com.project.ayd.mechanic_workshop.features.reports.exception;

public class ReportNotFoundException extends ReportException {

    public ReportNotFoundException(String reportId) {
        super("Report not found or expired: " + reportId, "REPORT_NOT_FOUND");
    }

    public ReportNotFoundException(String message, Throwable cause) {
        super(message, "REPORT_NOT_FOUND", cause);
    }
}