package com.project.ayd.mechanic_workshop.features.users.controller;

import com.project.ayd.mechanic_workshop.features.users.dto.SpecializationTypeResponse;
import com.project.ayd.mechanic_workshop.features.users.service.SpecializationTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/specialization-types")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO', 'ESPECIALISTA')")
public class SpecializationTypeController {

    private final SpecializationTypeService specializationTypeService;

    @GetMapping
    public ResponseEntity<List<SpecializationTypeResponse>> getAllSpecializationTypes() {
        List<SpecializationTypeResponse> specializationTypes = specializationTypeService.getAllSpecializationTypes();
        return ResponseEntity.ok(specializationTypes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpecializationTypeResponse> getSpecializationTypeById(@PathVariable Long id) {
        SpecializationTypeResponse specializationType = specializationTypeService.getSpecializationTypeById(id);
        return ResponseEntity.ok(specializationType);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<SpecializationTypeResponse> getSpecializationTypeByName(@PathVariable String name) {
        SpecializationTypeResponse specializationType = specializationTypeService.getSpecializationTypeByName(name);
        return ResponseEntity.ok(specializationType);
    }
}