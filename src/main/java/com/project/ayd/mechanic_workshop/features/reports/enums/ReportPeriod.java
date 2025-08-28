package com.project.ayd.mechanic_workshop.features.reports.enums;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public enum ReportPeriod {
    TODAY("Hoy", 0, ChronoUnit.DAYS),
    LAST_7_DAYS("Últimos 7 días", 7, ChronoUnit.DAYS),
    LAST_MONTH("Último mes", 1, ChronoUnit.MONTHS),
    LAST_3_MONTHS("Últimos 3 meses", 3, ChronoUnit.MONTHS),
    LAST_6_MONTHS("Últimos 6 meses", 6, ChronoUnit.MONTHS),
    CURRENT_YEAR("Año actual", 1, ChronoUnit.YEARS),
    CUSTOM("Personalizado", 0, ChronoUnit.DAYS);

    private final String displayName;
    private final long amount;
    private final ChronoUnit unit;

    ReportPeriod(String displayName, long amount, ChronoUnit unit) {
        this.displayName = displayName;
        this.amount = amount;
        this.unit = unit;
    }

    public String getDisplayName() {
        return displayName;
    }

    public LocalDateTime getStartDate() {
        if (this == TODAY) {
            return LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        } else if (this == CURRENT_YEAR) {
            return LocalDateTime.now().withDayOfYear(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        } else if (this == CUSTOM) {
            return null;
        }
        return LocalDateTime.now().minus(amount, unit);
    }

    public LocalDateTime getEndDate() {
        if (this == CUSTOM) {
            return null;
        }
        return LocalDateTime.now();
    }
}