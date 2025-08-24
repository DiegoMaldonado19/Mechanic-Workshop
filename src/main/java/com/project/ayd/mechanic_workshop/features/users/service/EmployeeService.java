package com.project.ayd.mechanic_workshop.features.users.service;

import com.project.ayd.mechanic_workshop.features.users.dto.EmployeeRequest;
import com.project.ayd.mechanic_workshop.features.users.dto.EmployeeSpecializationRequest;
import com.project.ayd.mechanic_workshop.features.users.dto.UserResponse;

import java.util.List;

public interface EmployeeService {

    UserResponse createEmployee(EmployeeRequest request);

    List<UserResponse> getAllEmployees();

    List<UserResponse> getAllActiveEmployees();

    UserResponse getEmployeeById(Long employeeId);

    UserResponse getEmployeeByCui(String cui);

    void addSpecializationToEmployee(Long employeeId, EmployeeSpecializationRequest request);

    void removeSpecializationFromEmployee(Long employeeId, Long specializationId);

    List<UserResponse> getEmployeesBySpecialization(Long specializationTypeId);
}