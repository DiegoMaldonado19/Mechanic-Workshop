package com.project.ayd.mechanic_workshop.features.reports.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ClientServiceReportDTO {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<ClientDetail> clientDetails;
    private List<ClientRating> clientRatings;
    private ClientSummary summary;

    @Data
    @Builder
    public static class ClientDetail {
        private String clientName;
        private String clientCui;
        private String clientEmail;
        private Integer totalWorks;
        private BigDecimal totalSpent;
        private LocalDateTime lastVisit;
        private Integer vehiclesCount;
        private String serviceTypes;
    }

    @Data
    @Builder
    public static class ClientRating {
        private String clientName;
        private Integer totalRatings;
        private BigDecimal averageRating;
        private Integer recommendations;
        private Integer positiveRatings;
    }

    @Data
    @Builder
    public static class ClientSummary {
        private Integer totalClients;
        private BigDecimal totalRevenue;
        private BigDecimal averageSpentPerClient;
        private BigDecimal averageRating;
        private Integer totalRatings;
    }
}