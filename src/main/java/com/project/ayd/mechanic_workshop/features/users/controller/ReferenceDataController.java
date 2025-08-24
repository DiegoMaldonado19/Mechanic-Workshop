package com.project.ayd.mechanic_workshop.features.users.controller;

import com.project.ayd.mechanic_workshop.features.auth.entity.Gender;
import com.project.ayd.mechanic_workshop.features.auth.entity.UserType;
import com.project.ayd.mechanic_workshop.features.auth.repository.GenderRepository;
import com.project.ayd.mechanic_workshop.features.auth.repository.UserTypeRepository;
import com.project.ayd.mechanic_workshop.features.common.dto.*;
import com.project.ayd.mechanic_workshop.features.common.service.GeographicDataService;
import com.project.ayd.mechanic_workshop.features.users.dto.SpecializationTypeResponse;
import com.project.ayd.mechanic_workshop.features.users.service.SpecializationTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reference")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO', 'ESPECIALISTA', 'CLIENTE', 'PROVEEDOR')")
public class ReferenceDataController {

    private final GenderRepository genderRepository;
    private final UserTypeRepository userTypeRepository;
    private final GeographicDataService geographicDataService;
    private final SpecializationTypeService specializationTypeService;

    // ===== GÉNEROS =====
    @GetMapping("/genders")
    public ResponseEntity<List<Gender>> getAllGenders() {
        List<Gender> genders = genderRepository.findAll();
        return ResponseEntity.ok(genders);
    }

    @GetMapping("/genders/{id}")
    public ResponseEntity<Gender> getGenderById(@PathVariable Long id) {
        Gender gender = genderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Gender not found with ID: " + id));
        return ResponseEntity.ok(gender);
    }

    // ===== TIPOS DE USUARIO =====
    @GetMapping("/user-types")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<UserType>> getAllUserTypes() {
        List<UserType> userTypes = userTypeRepository.findAll();
        return ResponseEntity.ok(userTypes);
    }

    @GetMapping("/user-types/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UserType> getUserTypeById(@PathVariable Long id) {
        UserType userType = userTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User type not found with ID: " + id));
        return ResponseEntity.ok(userType);
    }

    // ===== DEPARTAMENTOS =====
    @GetMapping("/departments")
    public ResponseEntity<List<DepartmentResponse>> getAllDepartments() {
        List<DepartmentResponse> departments = geographicDataService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/departments/{id}")
    public ResponseEntity<DepartmentResponse> getDepartmentById(@PathVariable Long id) {
        DepartmentResponse department = geographicDataService.getDepartmentById(id);
        return ResponseEntity.ok(department);
    }

    // ===== MUNICIPIOS =====
    @GetMapping("/municipalities")
    public ResponseEntity<List<MunicipalityResponse>> getAllMunicipalities() {
        List<MunicipalityResponse> municipalities = geographicDataService.getAllMunicipalities();
        return ResponseEntity.ok(municipalities);
    }

    @GetMapping("/municipalities/{id}")
    public ResponseEntity<MunicipalityResponse> getMunicipalityById(@PathVariable Long id) {
        MunicipalityResponse municipality = geographicDataService.getMunicipalityById(id);
        return ResponseEntity.ok(municipality);
    }

    @GetMapping("/municipalities/by-department/{departmentId}")
    public ResponseEntity<List<MunicipalityResponse>> getMunicipalitiesByDepartment(@PathVariable Long departmentId) {
        List<MunicipalityResponse> municipalities = geographicDataService.getMunicipalitiesByDepartment(departmentId);
        return ResponseEntity.ok(municipalities);
    }

    // ===== DIRECCIONES =====
    @GetMapping("/addresses")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROVEEDOR')")
    public ResponseEntity<List<AddressDetailResponse>> getAddressDetailsByMunicipality(
            @RequestParam Long municipalityId) {
        List<AddressDetailResponse> addresses = geographicDataService.getAddressDetailsByMunicipality(municipalityId);
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/addresses/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROVEEDOR')")
    public ResponseEntity<AddressDetailResponse> getAddressDetailById(@PathVariable Long id) {
        AddressDetailResponse address = geographicDataService.getAddressDetailById(id);
        return ResponseEntity.ok(address);
    }

    @PostMapping("/addresses")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROVEEDOR')")
    public ResponseEntity<AddressDetailResponse> createAddressDetail(@Valid @RequestBody AddressDetailRequest request) {
        AddressDetailResponse address = geographicDataService.createAddressDetail(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(address);
    }

    // ===== TIPOS DE ESPECIALIZACIÓN =====
    @GetMapping("/specialization-types")
    public ResponseEntity<List<SpecializationTypeResponse>> getAllSpecializationTypes() {
        List<SpecializationTypeResponse> specializationTypes = specializationTypeService.getAllSpecializationTypes();
        return ResponseEntity.ok(specializationTypes);
    }

    @GetMapping("/specialization-types/{id}")
    public ResponseEntity<SpecializationTypeResponse> getSpecializationTypeById(@PathVariable Long id) {
        SpecializationTypeResponse specializationType = specializationTypeService.getSpecializationTypeById(id);
        return ResponseEntity.ok(specializationType);
    }
}