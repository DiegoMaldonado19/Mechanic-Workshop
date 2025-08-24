package com.project.ayd.mechanic_workshop.features.users.service;

import com.project.ayd.mechanic_workshop.features.users.dto.SpecializationTypeResponse;

import java.util.List;

public interface SpecializationTypeService {

    List<SpecializationTypeResponse> getAllSpecializationTypes();

    SpecializationTypeResponse getSpecializationTypeById(Long id);

    SpecializationTypeResponse getSpecializationTypeByName(String name);
}