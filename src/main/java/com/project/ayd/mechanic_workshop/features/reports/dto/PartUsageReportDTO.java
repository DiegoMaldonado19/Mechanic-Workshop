package com.project.ayd.mechanic_workshop.features.reports.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PartUsageReportDTO {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<PartUsageDetail> partUsageDetails;
    private PartUsageSummary summary;

    @Data
    @Builder
    public static class PartUsageDetail {
        private String partName;
        private String categoryName;
        private Integer totalQuantity;
        private BigDecimal totalCost;
        private Integer worksCount;
        private BigDecimal averageUnitPrice;
    }

    @Data
    @Builder
    public static class PartUsageSummary {
        private Integer totalDifferentParts;
        private Integer totalQuantityUsed;
        private BigDecimal totalCost;
        private Integer totalWorks;
    }
}