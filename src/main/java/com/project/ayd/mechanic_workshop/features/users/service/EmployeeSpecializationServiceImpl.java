package com.project.ayd.mechanic_workshop.features.users.service;

import com.project.ayd.mechanic_workshop.features.users.dto.EmployeeSpecializationRequest;
import com.project.ayd.mechanic_workshop.features.users.dto.EmployeeSpecializationResponse;
import com.project.ayd.mechanic_workshop.features.users.dto.SpecializationTypeResponse;
import com.project.ayd.mechanic_workshop.features.users.entity.EmployeeSpecialization;
import com.project.ayd.mechanic_workshop.features.users.repository.EmployeeSpecializationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeSpecializationServiceImpl implements EmployeeSpecializationService {

    private final EmployeeSpecializationRepository employeeSpecializationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeSpecializationResponse> getSpecializationsByEmployee(Long employeeId) {
        return employeeSpecializationRepository.findByUserId(employeeId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EmployeeSpecializationResponse updateSpecialization(Long specializationId,
            EmployeeSpecializationRequest request) {
        EmployeeSpecialization specialization = employeeSpecializationRepository.findById(specializationId)
                .orElseThrow(
                        () -> new IllegalArgumentException("Specialization not found with ID: " + specializationId));

        if (request.getCertificationDate() != null) {
            specialization.setCertificationDate(request.getCertificationDate());
        }
        if (request.getIsActive() != null) {
            specialization.setIsActive(request.getIsActive());
        }

        specialization = employeeSpecializationRepository.save(specialization);
        log.info("Employee specialization updated with ID: {}", specializationId);

        return mapToResponse(specialization);
    }

    @Override
    @Transactional
    public void activateSpecialization(Long specializationId) {
        EmployeeSpecialization specialization = employeeSpecializationRepository.findById(specializationId)
                .orElseThrow(
                        () -> new IllegalArgumentException("Specialization not found with ID: " + specializationId));

        specialization.setIsActive(true);
        employeeSpecializationRepository.save(specialization);
        log.info("Employee specialization activated with ID: {}", specializationId);
    }

    @Override
    @Transactional
    public void deactivateSpecialization(Long specializationId) {
        EmployeeSpecialization specialization = employeeSpecializationRepository.findById(specializationId)
                .orElseThrow(
                        () -> new IllegalArgumentException("Specialization not found with ID: " + specializationId));

        specialization.setIsActive(false);
        employeeSpecializationRepository.save(specialization);
        log.info("Employee specialization deactivated with ID: {}", specializationId);
    }

    private EmployeeSpecializationResponse mapToResponse(EmployeeSpecialization specialization) {
        return EmployeeSpecializationResponse.builder()
                .id(specialization.getId())
                .userId(specialization.getUser().getId())
                .userName(specialization.getUser().getUsername())
                .specializationType(SpecializationTypeResponse.builder()
                        .id(specialization.getSpecializationType().getId())
                        .name(specialization.getSpecializationType().getName())
                        .description(specialization.getSpecializationType().getDescription())
                        .createdAt(specialization.getSpecializationType().getCreatedAt())
                        .build())
                .certificationDate(specialization.getCertificationDate())
                .isActive(specialization.getIsActive())
                .createdAt(specialization.getCreatedAt())
                .build();
    }
}