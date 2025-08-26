package com.project.ayd.mechanic_workshop.features.workorders.service;

import com.project.ayd.mechanic_workshop.features.auth.entity.User;
import com.project.ayd.mechanic_workshop.features.auth.repository.UserRepository;
import com.project.ayd.mechanic_workshop.features.users.dto.UserResponse;
import com.project.ayd.mechanic_workshop.features.workorders.dto.AssignWorkOrderRequest;
import com.project.ayd.mechanic_workshop.features.workorders.dto.WorkOrderResponse;
import com.project.ayd.mechanic_workshop.features.workorders.entity.Work;
import com.project.ayd.mechanic_workshop.features.workorders.entity.WorkStatus;
import com.project.ayd.mechanic_workshop.features.workorders.enums.Priority;
import com.project.ayd.mechanic_workshop.features.workorders.enums.WorkOrderStatus;
import com.project.ayd.mechanic_workshop.features.workorders.repository.WorkRepository;
import com.project.ayd.mechanic_workshop.features.workorders.repository.WorkStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkOrderAssignmentServiceImpl implements WorkOrderAssignmentService {

    private final WorkRepository workRepository;
    private final WorkStatusRepository workStatusRepository;
    private final UserRepository userRepository;

    private static final int DEFAULT_MAX_WORKORDERS_PER_EMPLOYEE = 5;

    @Override
    @Transactional
    public WorkOrderResponse assignWorkOrder(Long workOrderId, AssignWorkOrderRequest request) {
        log.info("Assigning work order {} to employee {}", workOrderId, request.getEmployeeId());

        Work work = workRepository.findById(workOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Work order not found with ID: " + workOrderId));

        // Verificar que la orden esté en estado pendiente
        if (!work.getWorkStatus().getId().equals(WorkOrderStatus.PENDING.getId())) {
            throw new IllegalStateException("Work order must be in pending status to be assigned");
        }

        User employee = userRepository.findById(request.getEmployeeId())
                .orElseThrow(
                        () -> new IllegalArgumentException("Employee not found with ID: " + request.getEmployeeId()));

        // Verificar que el empleado no esté sobrecargado
        Long currentWorkload = workRepository.countActiveWorksByEmployeeId(request.getEmployeeId());
        if (currentWorkload >= DEFAULT_MAX_WORKORDERS_PER_EMPLOYEE) {
            throw new IllegalStateException("Employee has reached maximum work order capacity");
        }

        WorkStatus assignedStatus = workStatusRepository.findById(WorkOrderStatus.ASSIGNED.getId())
                .orElseThrow(() -> new IllegalArgumentException("Assigned status not found"));

        work.setAssignedEmployee(employee);
        work.setWorkStatus(assignedStatus);

        work = workRepository.save(work);
        log.info("Work order assigned successfully");

        return mapToWorkOrderResponse(work);
    }

    @Override
    @Transactional
    public WorkOrderResponse reassignWorkOrder(Long workOrderId, Long newEmployeeId) {
        log.info("Reassigning work order {} to new employee {}", workOrderId, newEmployeeId);

        Work work = workRepository.findById(workOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Work order not found with ID: " + workOrderId));

        // Solo permitir reasignación si no está en progreso o completado
        Long currentStatusId = work.getWorkStatus().getId();
        if (currentStatusId.equals(WorkOrderStatus.IN_PROGRESS.getId()) ||
                currentStatusId.equals(WorkOrderStatus.COMPLETED.getId())) {
            throw new IllegalStateException("Cannot reassign work order in progress or completed status");
        }

        User newEmployee = userRepository.findById(newEmployeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + newEmployeeId));

        // Verificar capacidad del nuevo empleado
        Long currentWorkload = workRepository.countActiveWorksByEmployeeId(newEmployeeId);
        if (currentWorkload >= DEFAULT_MAX_WORKORDERS_PER_EMPLOYEE) {
            throw new IllegalStateException("New employee has reached maximum work order capacity");
        }

        work.setAssignedEmployee(newEmployee);
        work = workRepository.save(work);

        log.info("Work order reassigned successfully");
        return mapToWorkOrderResponse(work);
    }

    @Override
    @Transactional
    public WorkOrderResponse unassignWorkOrder(Long workOrderId) {
        log.info("Unassigning work order {}", workOrderId);

        Work work = workRepository.findById(workOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Work order not found with ID: " + workOrderId));

        // Solo permitir desasignación si no está en progreso
        if (work.getWorkStatus().getId().equals(WorkOrderStatus.IN_PROGRESS.getId())) {
            throw new IllegalStateException("Cannot unassign work order in progress");
        }

        WorkStatus pendingStatus = workStatusRepository.findById(WorkOrderStatus.PENDING.getId())
                .orElseThrow(() -> new IllegalArgumentException("Pending status not found"));

        work.setAssignedEmployee(null);
        work.setWorkStatus(pendingStatus);

        work = workRepository.save(work);
        log.info("Work order unassigned successfully");

        return mapToWorkOrderResponse(work);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAvailableEmployeesForAssignment() {
        // Obtener empleados que no han alcanzado la capacidad máxima
        return userRepository.findAll().stream()
                .filter(user -> {
                    // Filtrar solo empleados y especialistas
                    String userType = user.getUserType().getName();
                    return "EMPLEADO".equals(userType) || "ESPECIALISTA".equals(userType);
                })
                .filter(user -> {
                    Long workload = workRepository.countActiveWorksByEmployeeId(user.getId());
                    return workload < DEFAULT_MAX_WORKORDERS_PER_EMPLOYEE;
                })
                .map(this::mapToUserResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getEmployeesWithCapacity(int maxWorkOrders) {
        return userRepository.findAll().stream()
                .filter(user -> {
                    String userType = user.getUserType().getName();
                    return "EMPLEADO".equals(userType) || "ESPECIALISTA".equals(userType);
                })
                .filter(user -> {
                    Long workload = workRepository.countActiveWorksByEmployeeId(user.getId());
                    return workload < maxWorkOrders;
                })
                .map(this::mapToUserResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmployeeAvailable(Long employeeId) {
        Long currentWorkload = workRepository.countActiveWorksByEmployeeId(employeeId);
        return currentWorkload < DEFAULT_MAX_WORKORDERS_PER_EMPLOYEE;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getEmployeeWorkload(Long employeeId) {
        return workRepository.countActiveWorksByEmployeeId(employeeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkOrderResponse> getUnassignedWorkOrders() {
        Page<Work> workPage = workRepository.findByWorkStatusId(WorkOrderStatus.PENDING.getId(),
                PageRequest.of(0, Integer.MAX_VALUE));

        return workPage.getContent()
                .stream()
                .filter(work -> work.getAssignedEmployee() == null)
                .map(this::mapToWorkOrderResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public WorkOrderResponse findBestEmployeeForWorkOrder(Long workOrderId) {
        Work work = workRepository.findById(workOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Work order not found with ID: " + workOrderId));

        // Verificar que la orden esté en estado pendiente
        if (!work.getWorkStatus().getId().equals(WorkOrderStatus.PENDING.getId())) {
            throw new IllegalStateException("Work order must be in pending status to be auto-assigned");
        }

        // Encontrar el empleado con menor carga de trabajo
        Optional<User> bestEmployee = userRepository.findAll().stream()
                .filter(user -> {
                    String userType = user.getUserType().getName();
                    return "EMPLEADO".equals(userType) || "ESPECIALISTA".equals(userType);
                })
                .filter(user -> {
                    Long workload = workRepository.countActiveWorksByEmployeeId(user.getId());
                    return workload < DEFAULT_MAX_WORKORDERS_PER_EMPLOYEE;
                })
                .min((u1, u2) -> {
                    Long workload1 = workRepository.countActiveWorksByEmployeeId(u1.getId());
                    Long workload2 = workRepository.countActiveWorksByEmployeeId(u2.getId());
                    return workload1.compareTo(workload2);
                });

        if (bestEmployee.isPresent()) {
            AssignWorkOrderRequest assignRequest = AssignWorkOrderRequest.builder()
                    .employeeId(bestEmployee.get().getId())
                    .assignmentNotes("Automatically assigned to employee with lowest workload")
                    .build();

            return assignWorkOrder(workOrderId, assignRequest);
        } else {
            throw new IllegalStateException("No available employees found for assignment");
        }
    }

    private WorkOrderResponse mapToWorkOrderResponse(Work work) {
        String priorityDisplayName = work.getPriorityLevel() != null
                ? Priority.fromLevel(work.getPriorityLevel()).getDisplayName()
                : null;

        return WorkOrderResponse.builder()
                .id(work.getId())
                .problemDescription(work.getProblemDescription())
                .estimatedHours(work.getEstimatedHours())
                .actualHours(work.getActualHours())
                .estimatedCost(work.getEstimatedCost())
                .actualCost(work.getActualCost())
                .clientApproved(work.getClientApproved())
                .clientApprovedAt(work.getClientApprovedAt())
                .startedAt(work.getStartedAt())
                .completedAt(work.getCompletedAt())
                .priorityLevel(work.getPriorityLevel())
                .priorityDisplayName(priorityDisplayName)
                .createdAt(work.getCreatedAt())
                .updatedAt(work.getUpdatedAt())
                .build();
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getPerson() != null ? user.getPerson().getFirstName() : null)
                .lastName(user.getPerson() != null ? user.getPerson().getLastName() : null)
                .email(user.getPerson() != null ? user.getPerson().getEmail() : null)
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}