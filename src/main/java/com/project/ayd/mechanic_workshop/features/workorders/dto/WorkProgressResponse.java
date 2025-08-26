package com.project.ayd.mechanic_workshop.features.workorders.dto;

import com.project.ayd.mechanic_workshop.features.users.dto.UserResponse;
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
public class WorkProgressResponse {

    private Long id;
    private Long workId;
    private UserResponse user;
    private String progressDescription;
    private BigDecimal hoursWorked;
    private String observations;
    private String symptomsDetected;
    private String additionalDamageFound;
    private LocalDateTime recordedAt;
}