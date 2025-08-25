package com.project.ayd.mechanic_workshop.features.workorders.service;

import com.project.ayd.mechanic_workshop.features.workorders.dto.AssignWorkOrderRequest;
import com.project.ayd.mechanic_workshop.features.workorders.dto.WorkOrderResponse;
import com.project.ayd.mechanic_workshop.features.users.dto.UserResponse;

import java.util.List;

public interface WorkOrderAssignmentService {

    WorkOrderResponse assignWorkOrder(Long workOrderId, AssignWorkOrderRequest request);

    WorkOrderResponse reassignWorkOrder(Long workOrderId, Long newEmployeeId);

    WorkOrderResponse unassignWorkOrder(Long workOrderId);

    List<UserResponse> getAvailableEmployeesForAssignment();

    List<UserResponse> getEmployeesWithCapacity(int maxWorkOrders);

    boolean isEmployeeAvailable(Long employeeId);

    Long getEmployeeWorkload(Long employeeId);

    List<WorkOrderResponse> getUnassignedWorkOrders();

    WorkOrderResponse findBestEmployeeForWorkOrder(Long workOrderId);
}