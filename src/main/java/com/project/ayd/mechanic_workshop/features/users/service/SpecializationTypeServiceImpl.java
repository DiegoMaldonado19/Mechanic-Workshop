package com.project.ayd.mechanic_workshop.features.users.service;

import com.project.ayd.mechanic_workshop.features.users.dto.SpecializationTypeResponse;
import com.project.ayd.mechanic_workshop.features.users.entity.SpecializationType;
import com.project.ayd.mechanic_workshop.features.users.repository.SpecializationTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpecializationTypeServiceImpl implements SpecializationTypeService {

    private final SpecializationTypeRepository specializationTypeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SpecializationTypeResponse> getAllSpecializationTypes() {
        return specializationTypeRepository.findAll().stream()
                .map(this::mapToSpecializationTypeResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SpecializationTypeResponse getSpecializationTypeById(Long id) {
        SpecializationType specializationType = specializationTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Specialization type not found with ID: " + id));
        return mapToSpecializationTypeResponse(specializationType);
    }

    @Override
    @Transactional(readOnly = true)
    public SpecializationTypeResponse getSpecializationTypeByName(String name) {
        SpecializationType specializationType = specializationTypeRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Specialization type not found with name: " + name));
        return mapToSpecializationTypeResponse(specializationType);
    }

    private SpecializationTypeResponse mapToSpecializationTypeResponse(SpecializationType specializationType) {
        return SpecializationTypeResponse.builder()
                .id(specializationType.getId())
                .name(specializationType.getName())
                .description(specializationType.getDescription())
                .createdAt(specializationType.getCreatedAt())
                .build();
    }
}