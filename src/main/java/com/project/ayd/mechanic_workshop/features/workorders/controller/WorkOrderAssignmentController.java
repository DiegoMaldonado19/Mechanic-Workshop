package com.project.ayd.mechanic_workshop.features.workorders.controller;

import com.project.ayd.mechanic_workshop.features.users.dto.UserResponse;
import com.project.ayd.mechanic_workshop.features.workorders.dto.AssignWorkOrderRequest;
import com.project.ayd.mechanic_workshop.features.workorders.dto.WorkOrderResponse;
import com.project.ayd.mechanic_workshop.features.workorders.service.WorkOrderAssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/workorders/assignments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class WorkOrderAssignmentController {

    private final WorkOrderAssignmentService assignmentService;

    @PostMapping("/{workOrderId}")
    public ResponseEntity<WorkOrderResponse> assignWorkOrder(
            @PathVariable Long workOrderId,
            @Valid @RequestBody AssignWorkOrderRequest request) {
        WorkOrderResponse response = assignmentService.assignWorkOrder(workOrderId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{workOrderId}/reassign/{newEmployeeId}")
    public ResponseEntity<WorkOrderResponse> reassignWorkOrder(
            @PathVariable Long workOrderId,
            @PathVariable Long newEmployeeId) {
        WorkOrderResponse response = assignmentService.reassignWorkOrder(workOrderId, newEmployeeId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{workOrderId}/unassign")
    public ResponseEntity<WorkOrderResponse> unassignWorkOrder(@PathVariable Long workOrderId) {
        WorkOrderResponse response = assignmentService.unassignWorkOrder(workOrderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employees/available")
    public ResponseEntity<List<UserResponse>> getAvailableEmployeesForAssignment() {
        List<UserResponse> response = assignmentService.getAvailableEmployeesForAssignment();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employees/capacity")
    public ResponseEntity<List<UserResponse>> getEmployeesWithCapacity(
            @RequestParam(defaultValue = "5") int maxWorkOrders) {
        List<UserResponse> response = assignmentService.getEmployeesWithCapacity(maxWorkOrders);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employees/{employeeId}/available")
    public ResponseEntity<Map<String, Boolean>> isEmployeeAvailable(@PathVariable Long employeeId) {
        boolean isAvailable = assignmentService.isEmployeeAvailable(employeeId);
        return ResponseEntity.ok(Map.of("isAvailable", isAvailable));
    }

    @GetMapping("/employees/{employeeId}/workload")
    public ResponseEntity<Map<String, Long>> getEmployeeWorkload(@PathVariable Long employeeId) {
        Long workload = assignmentService.getEmployeeWorkload(employeeId);
        return ResponseEntity.ok(Map.of("workload", workload));
    }

    @GetMapping("/unassigned")
    public ResponseEntity<List<WorkOrderResponse>> getUnassignedWorkOrders() {
        List<WorkOrderResponse> response = assignmentService.getUnassignedWorkOrders();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{workOrderId}/auto-assign")
    public ResponseEntity<WorkOrderResponse> autoAssignWorkOrder(@PathVariable Long workOrderId) {
        WorkOrderResponse response = assignmentService.findBestEmployeeForWorkOrder(workOrderId);
        return ResponseEntity.ok(response);
    }
}