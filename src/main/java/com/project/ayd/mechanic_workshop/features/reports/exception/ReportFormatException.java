package com.project.ayd.mechanic_workshop.features.reports.exception;

public class ReportFormatException extends ReportException {

    public ReportFormatException(String format) {
        super("Unsupported report format: " + format, "UNSUPPORTED_FORMAT");
    }

    public ReportFormatException(String message, Throwable cause) {
        super(message, "FORMAT_ERROR", cause);
    }
}