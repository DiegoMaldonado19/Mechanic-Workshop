package com.project.ayd.mechanic_workshop.features.users.service;

import com.project.ayd.mechanic_workshop.features.users.dto.SpecialistRequest;
import com.project.ayd.mechanic_workshop.features.users.dto.UserResponse;

import java.util.List;

public interface SpecialistService {

    UserResponse createSpecialist(SpecialistRequest request);

    List<UserResponse> getAllSpecialists();

    List<UserResponse> getAllActiveSpecialists();

    UserResponse getSpecialistById(Long specialistId);

    List<UserResponse> getSpecialistsBySpecializationType(Long specializationTypeId);
}