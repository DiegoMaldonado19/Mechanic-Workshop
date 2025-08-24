package com.project.ayd.mechanic_workshop.features.users.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeSpecializationRequest {

    @NotNull(message = "Specialization type is required")
    private Long specializationTypeId;

    private LocalDate certificationDate;

    private Boolean isActive = true;
}