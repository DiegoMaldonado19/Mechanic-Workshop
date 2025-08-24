package com.project.ayd.mechanic_workshop.features.users.controller;

import com.project.ayd.mechanic_workshop.features.users.dto.SpecialistRequest;
import com.project.ayd.mechanic_workshop.features.users.dto.UserResponse;
import com.project.ayd.mechanic_workshop.features.users.service.SpecialistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/specialists")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ESPECIALISTA')")
public class SpecialistController {

    private final SpecialistService specialistService;

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<UserResponse>> getAllSpecialists() {
        List<UserResponse> specialists = specialistService.getAllSpecialists();
        return ResponseEntity.ok(specialists);
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<UserResponse>> getAllActiveSpecialists() {
        List<UserResponse> specialists = specialistService.getAllActiveSpecialists();
        return ResponseEntity.ok(specialists);
    }

    @GetMapping("/{specialistId}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or @specialistService.getSpecialistById(#specialistId).username == authentication.name")
    public ResponseEntity<UserResponse> getSpecialistById(@PathVariable Long specialistId) {
        UserResponse specialist = specialistService.getSpecialistById(specialistId);
        return ResponseEntity.ok(specialist);
    }

    @GetMapping("/specialization/{specializationTypeId}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<UserResponse>> getSpecialistsBySpecializationType(
            @PathVariable Long specializationTypeId) {
        List<UserResponse> specialists = specialistService.getSpecialistsBySpecializationType(specializationTypeId);
        return ResponseEntity.ok(specialists);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UserResponse> createSpecialist(@Valid @RequestBody SpecialistRequest request) {
        UserResponse specialist = specialistService.createSpecialist(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(specialist);
    }
}