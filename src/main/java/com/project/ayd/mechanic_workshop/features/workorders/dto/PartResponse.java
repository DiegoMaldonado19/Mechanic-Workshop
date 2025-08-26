package com.project.ayd.mechanic_workshop.features.workorders.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartResponse {

    private Long id;
    private String name;
    private String partNumber;
    private String description;
    private PartCategoryResponse category;
    private BigDecimal unitPrice;
    private Integer minimumStock;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}