package com.project.ayd.mechanic_workshop.features.reports.enums;

import java.time.LocalDateTime;

public enum ReportPeriod {
    TODAY("Hoy"),
    YESTERDAY("Ayer"),
    THIS_WEEK("Esta Semana"),
    LAST_WEEK("Semana Pasada"),
    THIS_MONTH("Este Mes"),
    LAST_MONTH("Mes Pasado"),
    THIS_QUARTER("Este Trimestre"),
    LAST_QUARTER("Trimestre Pasado"),
    THIS_YEAR("Este Año"),
    LAST_YEAR("Año Pasado"),
    LAST_30_DAYS("Últimos 30 Días"),
    LAST_60_DAYS("Últimos 60 Días"),
    LAST_90_DAYS("Últimos 90 Días"),
    LAST_6_MONTHS("Últimos 6 Meses"),
    LAST_12_MONTHS("Últimos 12 Meses"),
    CUSTOM("Personalizado");

    private final String displayName;

    ReportPeriod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public LocalDateTime getStartDate() {
        LocalDateTime now = LocalDateTime.now();
        return switch (this) {
            case TODAY -> now.withHour(0).withMinute(0).withSecond(0).withNano(0);
            case YESTERDAY -> now.minusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            case THIS_WEEK ->
                now.minusDays(now.getDayOfWeek().getValue() - 1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            case LAST_WEEK ->
                now.minusDays(now.getDayOfWeek().getValue() + 6).withHour(0).withMinute(0).withSecond(0).withNano(0);
            case THIS_MONTH -> now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            case LAST_MONTH -> now.minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            case THIS_QUARTER -> now.minusMonths((now.getMonthValue() - 1) % 3).withDayOfMonth(1).withHour(0)
                    .withMinute(0).withSecond(0).withNano(0);
            case LAST_QUARTER -> now.minusMonths(3 + (now.getMonthValue() - 1) % 3).withDayOfMonth(1).withHour(0)
                    .withMinute(0).withSecond(0).withNano(0);
            case THIS_YEAR -> now.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            case LAST_YEAR -> now.minusYears(1).withDayOfYear(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            case LAST_30_DAYS -> now.minusDays(30).withHour(0).withMinute(0).withSecond(0).withNano(0);
            case LAST_60_DAYS -> now.minusDays(60).withHour(0).withMinute(0).withSecond(0).withNano(0);
            case LAST_90_DAYS -> now.minusDays(90).withHour(0).withMinute(0).withSecond(0).withNano(0);
            case LAST_6_MONTHS -> now.minusMonths(6).withHour(0).withMinute(0).withSecond(0).withNano(0);
            case LAST_12_MONTHS -> now.minusYears(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            case CUSTOM -> null; // Se manejará externamente
        };
    }

    public LocalDateTime getEndDate() {
        LocalDateTime now = LocalDateTime.now();
        return switch (this) {
            case TODAY -> now.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
            case YESTERDAY -> now.minusDays(1).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
            case THIS_WEEK -> now.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
            case LAST_WEEK -> now.minusDays(now.getDayOfWeek().getValue()).withHour(23).withMinute(59).withSecond(59)
                    .withNano(999999999);
            case THIS_MONTH -> now.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
            case LAST_MONTH ->
                now.withDayOfMonth(1).minusDays(1).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
            case THIS_QUARTER -> now.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
            case LAST_QUARTER -> now.minusMonths((now.getMonthValue() - 1) % 3 + 1).withDayOfMonth(1).minusDays(1)
                    .withHour(23).withMinute(59).withSecond(59).withNano(999999999);
            case THIS_YEAR -> now.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
            case LAST_YEAR ->
                now.withDayOfYear(1).minusDays(1).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
            case LAST_30_DAYS, LAST_60_DAYS, LAST_90_DAYS, LAST_6_MONTHS, LAST_12_MONTHS ->
                now.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
            case CUSTOM -> null; // Se manejará externamente
        };
    }
}