package com.project.ayd.mechanic_workshop.features.reports.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class VehicleBrandReportDTO {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<BrandStatistic> brandStatistics;
    private BrandSummary summary;

    @Data
    @Builder
    public static class BrandStatistic {
        private String brandName;
        private Integer totalWorks;
        private BigDecimal averageCost;
        private Integer uniqueVehicles;
        private BigDecimal totalRevenue;
        private BigDecimal averageHours;
    }

    @Data
    @Builder
    public static class BrandSummary {
        private Integer totalBrands;
        private String mostServicedBrand;
        private String mostProfitableBrand;
        private BigDecimal totalRevenue;
    }
}
