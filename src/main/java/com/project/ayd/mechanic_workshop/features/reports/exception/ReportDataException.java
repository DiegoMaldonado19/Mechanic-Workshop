package com.project.ayd.mechanic_workshop.features.reports.exception;

public class ReportDataException extends ReportException {

    public ReportDataException(String message) {
        super(message, "REPORT_DATA_ERROR");
    }

    public ReportDataException(String message, Throwable cause) {
        super(message, "REPORT_DATA_ERROR", cause);
    }
}