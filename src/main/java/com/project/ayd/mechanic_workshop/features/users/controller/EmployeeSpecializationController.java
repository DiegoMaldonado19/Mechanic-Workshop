package com.project.ayd.mechanic_workshop.features.users.controller;

import com.project.ayd.mechanic_workshop.features.users.dto.EmployeeSpecializationRequest;
import com.project.ayd.mechanic_workshop.features.users.dto.EmployeeSpecializationResponse;
import com.project.ayd.mechanic_workshop.features.users.service.EmployeeSpecializationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employee-specializations")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class EmployeeSpecializationController {

    private final EmployeeSpecializationService employeeSpecializationService;

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<EmployeeSpecializationResponse>> getSpecializationsByEmployee(
            @PathVariable Long employeeId) {
        List<EmployeeSpecializationResponse> specializations = employeeSpecializationService
                .getSpecializationsByEmployee(employeeId);
        return ResponseEntity.ok(specializations);
    }

    @PutMapping("/{specializationId}")
    public ResponseEntity<EmployeeSpecializationResponse> updateSpecialization(
            @PathVariable Long specializationId,
            @Valid @RequestBody EmployeeSpecializationRequest request) {
        EmployeeSpecializationResponse specialization = employeeSpecializationService
                .updateSpecialization(specializationId, request);
        return ResponseEntity.ok(specialization);
    }

    @PutMapping("/{specializationId}/activate")
    public ResponseEntity<Map<String, String>> activateSpecialization(@PathVariable Long specializationId) {
        employeeSpecializationService.activateSpecialization(specializationId);
        return ResponseEntity.ok(Map.of("message", "Specialization activated successfully"));
    }

    @PutMapping("/{specializationId}/deactivate")
    public ResponseEntity<Map<String, String>> deactivateSpecialization(@PathVariable Long specializationId) {
        employeeSpecializationService.deactivateSpecialization(specializationId);
        return ResponseEntity.ok(Map.of("message", "Specialization deactivated successfully"));
    }
}