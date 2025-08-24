package com.project.ayd.mechanic_workshop.features.users.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class SpecialistRequest extends CreateUserRequest {

    @Valid
    @NotEmpty(message = "At least one specialization is required for specialists")
    private List<EmployeeSpecializationRequest> specializations;
}