package com.project.ayd.mechanic_workshop.features.users.controller;

import com.project.ayd.mechanic_workshop.features.users.dto.EmployeeRequest;
import com.project.ayd.mechanic_workshop.features.users.dto.EmployeeSpecializationRequest;
import com.project.ayd.mechanic_workshop.features.users.dto.UserResponse;
import com.project.ayd.mechanic_workshop.features.users.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO')")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<UserResponse>> getAllEmployees() {
        List<UserResponse> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<UserResponse>> getAllActiveEmployees() {
        List<UserResponse> employees = employeeService.getAllActiveEmployees();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{employeeId}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or @employeeService.getEmployeeById(#employeeId).username == authentication.name")
    public ResponseEntity<UserResponse> getEmployeeById(@PathVariable Long employeeId) {
        UserResponse employee = employeeService.getEmployeeById(employeeId);
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/cui/{cui}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UserResponse> getEmployeeByCui(@PathVariable String cui) {
        UserResponse employee = employeeService.getEmployeeByCui(cui);
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/specialization/{specializationTypeId}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<UserResponse>> getEmployeesBySpecialization(@PathVariable Long specializationTypeId) {
        List<UserResponse> employees = employeeService.getEmployeesBySpecialization(specializationTypeId);
        return ResponseEntity.ok(employees);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UserResponse> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        UserResponse employee = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(employee);
    }

    @PostMapping("/{employeeId}/specializations")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, String>> addSpecializationToEmployee(
            @PathVariable Long employeeId,
            @Valid @RequestBody EmployeeSpecializationRequest request) {
        employeeService.addSpecializationToEmployee(employeeId, request);
        return ResponseEntity.ok(Map.of("message", "Specialization added successfully"));
    }

    @DeleteMapping("/{employeeId}/specializations/{specializationId}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, String>> removeSpecializationFromEmployee(
            @PathVariable Long employeeId,
            @PathVariable Long specializationId) {
        employeeService.removeSpecializationFromEmployee(employeeId, specializationId);
        return ResponseEntity.ok(Map.of("message", "Specialization removed successfully"));
    }
}