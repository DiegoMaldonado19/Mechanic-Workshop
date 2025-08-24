package com.project.ayd.mechanic_workshop.features.users.service;

import com.project.ayd.mechanic_workshop.features.users.dto.EmployeeSpecializationRequest;
import com.project.ayd.mechanic_workshop.features.users.dto.EmployeeSpecializationResponse;

import java.util.List;

public interface EmployeeSpecializationService {

    List<EmployeeSpecializationResponse> getSpecializationsByEmployee(Long employeeId);

    EmployeeSpecializationResponse updateSpecialization(Long specializationId, EmployeeSpecializationRequest request);

    void activateSpecialization(Long specializationId);

    void deactivateSpecialization(Long specializationId);
}