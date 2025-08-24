package com.project.ayd.mechanic_workshop.features.users.dto;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class EmployeeRequest extends CreateUserRequest {

    @Valid
    private List<EmployeeSpecializationRequest> specializations;
}