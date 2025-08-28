package com.project.ayd.mechanic_workshop.features.reports.exception;

public class ReportException extends RuntimeException {

    private final String errorCode;

    public ReportException(String message) {
        super(message);
        this.errorCode = "REPORT_ERROR";
    }

    public ReportException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ReportException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "REPORT_ERROR";
    }

    public ReportException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}