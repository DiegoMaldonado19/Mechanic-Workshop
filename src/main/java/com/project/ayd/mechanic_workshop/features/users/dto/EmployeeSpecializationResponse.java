package com.project.ayd.mechanic_workshop.features.users.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeSpecializationResponse {
    private Long id;
    private Long userId;
    private String userName;
    private SpecializationTypeResponse specializationType;
    private LocalDate certificationDate;
    private Boolean isActive;
    private LocalDateTime createdAt;
}