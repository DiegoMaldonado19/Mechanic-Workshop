package com.project.ayd.mechanic_workshop.features.workorders.service;

import com.project.ayd.mechanic_workshop.features.auth.entity.User;
import com.project.ayd.mechanic_workshop.features.auth.repository.UserRepository;
import com.project.ayd.mechanic_workshop.features.users.dto.UserResponse;
import com.project.ayd.mechanic_workshop.features.vehicles.entity.Vehicle;
import com.project.ayd.mechanic_workshop.features.vehicles.repository.VehicleRepository;
import com.project.ayd.mechanic_workshop.features.workorders.dto.*;
import com.project.ayd.mechanic_workshop.features.workorders.entity.*;
import com.project.ayd.mechanic_workshop.features.workorders.enums.Priority;
import com.project.ayd.mechanic_workshop.features.workorders.enums.WorkOrderStatus;
import com.project.ayd.mechanic_workshop.features.workorders.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
// import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkOrderServiceImpl implements WorkOrderService {

        private final WorkRepository workRepository;
        private final WorkProgressRepository workProgressRepository;
        private final WorkPartRepository workPartRepository;
        private final WorkOrderQuotationRepository workOrderQuotationRepository;
        private final ServiceTypeRepository serviceTypeRepository;
        private final WorkStatusRepository workStatusRepository;
        private final PartRepository partRepository;
        private final VehicleRepository vehicleRepository;
        private final UserRepository userRepository;

        @Override
        @Transactional
        public WorkOrderResponse createWorkOrder(WorkOrderRequest request) {
                log.info("Creating work order for vehicle ID: {}", request.getVehicleId());

                Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Vehicle not found with ID: " + request.getVehicleId()));

                ServiceType serviceType = serviceTypeRepository.findById(request.getServiceTypeId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Service type not found with ID: " + request.getServiceTypeId()));

                WorkStatus pendingStatus = workStatusRepository.findById(WorkOrderStatus.PENDING.getId())
                                .orElseThrow(() -> new IllegalArgumentException("Pending status not found"));

                User currentUser = getCurrentUser();

                Work work = Work.builder()
                                .vehicle(vehicle)
                                .serviceType(serviceType)
                                .workStatus(pendingStatus)
                                .problemDescription(request.getProblemDescription())
                                .estimatedHours(request.getEstimatedHours())
                                .estimatedCost(request.getEstimatedCost())
                                .priorityLevel(request.getPriorityLevel())
                                .createdBy(currentUser)
                                .build();

                work = workRepository.save(work);
                log.info("Work order created successfully with ID: {}", work.getId());

                return mapToWorkOrderResponse(work);
        }

        @Override
        @Transactional(readOnly = true)
        public WorkOrderResponse getWorkOrderById(Long workOrderId) {
                Work work = workRepository.findByIdWithDetails(workOrderId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Work order not found with ID: " + workOrderId));
                return mapToWorkOrderResponse(work);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<WorkOrderResponse> getAllWorkOrders(Pageable pageable) {
                return workRepository.findAll(pageable)
                                .map(this::mapToWorkOrderResponse);
        }

        @Override
        @Transactional
        public WorkOrderResponse updateWorkOrder(Long workOrderId, UpdateWorkOrderRequest request) {
                log.info("Updating work order with ID: {}", workOrderId);

                Work work = workRepository.findById(workOrderId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Work order not found with ID: " + workOrderId));

                if (request.getProblemDescription() != null) {
                        work.setProblemDescription(request.getProblemDescription());
                }
                if (request.getEstimatedHours() != null) {
                        work.setEstimatedHours(request.getEstimatedHours());
                }
                if (request.getActualHours() != null) {
                        work.setActualHours(request.getActualHours());
                }
                if (request.getEstimatedCost() != null) {
                        work.setEstimatedCost(request.getEstimatedCost());
                }
                if (request.getActualCost() != null) {
                        work.setActualCost(request.getActualCost());
                }
                if (request.getPriorityLevel() != null) {
                        work.setPriorityLevel(request.getPriorityLevel());
                }
                if (request.getWorkStatusId() != null) {
                        WorkStatus workStatus = workStatusRepository.findById(request.getWorkStatusId())
                                        .orElseThrow(() -> new IllegalArgumentException(
                                                        "Work status not found with ID: " + request.getWorkStatusId()));
                        work.setWorkStatus(workStatus);
                }
                if (request.getAssignedEmployeeId() != null) {
                        User employee = userRepository.findById(request.getAssignedEmployeeId())
                                        .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: "
                                                        + request.getAssignedEmployeeId()));
                        work.setAssignedEmployee(employee);
                }

                work = workRepository.save(work);
                log.info("Work order updated successfully with ID: {}", workOrderId);

                return mapToWorkOrderResponse(work);
        }

        @Override
        @Transactional
        public void deleteWorkOrder(Long workOrderId) {
                log.info("Deleting work order with ID: {}", workOrderId);

                Work work = workRepository.findById(workOrderId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Work order not found with ID: " + workOrderId));

                // Solo permitir eliminación si está en estado pendiente
                if (!work.getWorkStatus().getId().equals(WorkOrderStatus.PENDING.getId())) {
                        throw new IllegalStateException("Work order can only be deleted when in pending status");
                }

                workRepository.delete(work);
                log.info("Work order deleted successfully with ID: {}", workOrderId);
        }

        @Override
        @Transactional
        public WorkOrderResponse assignWorkOrder(Long workOrderId, AssignWorkOrderRequest request) {
                log.info("Assigning work order {} to employee {}", workOrderId, request.getEmployeeId());

                Work work = workRepository.findById(workOrderId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Work order not found with ID: " + workOrderId));

                User employee = userRepository.findById(request.getEmployeeId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Employee not found with ID: " + request.getEmployeeId()));

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
        public WorkOrderResponse startWorkOrder(Long workOrderId) {
                log.info("Starting work order with ID: {}", workOrderId);

                Work work = workRepository.findById(workOrderId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Work order not found with ID: " + workOrderId));

                if (work.getAssignedEmployee() == null) {
                        throw new IllegalStateException("Work order must be assigned before it can be started");
                }

                WorkStatus inProgressStatus = workStatusRepository.findById(WorkOrderStatus.IN_PROGRESS.getId())
                                .orElseThrow(() -> new IllegalArgumentException("In progress status not found"));

                work.setWorkStatus(inProgressStatus);
                work.setStartedAt(LocalDateTime.now());

                work = workRepository.save(work);
                log.info("Work order started successfully");

                return mapToWorkOrderResponse(work);
        }

        @Override
        @Transactional
        public WorkOrderResponse completeWorkOrder(Long workOrderId) {
                log.info("Completing work order with ID: {}", workOrderId);

                Work work = workRepository.findById(workOrderId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Work order not found with ID: " + workOrderId));

                WorkStatus completedStatus = workStatusRepository.findById(WorkOrderStatus.COMPLETED.getId())
                                .orElseThrow(() -> new IllegalArgumentException("Completed status not found"));

                work.setWorkStatus(completedStatus);
                work.setCompletedAt(LocalDateTime.now());

                // Calcular horas trabajadas totales
                BigDecimal totalHours = workProgressRepository.sumHoursWorkedByWorkId(workOrderId);
                if (totalHours != null) {
                        work.setActualHours(totalHours);
                }

                // Calcular costo total de partes
                BigDecimal totalPartsCost = workPartRepository.calculateTotalPartsCostByWorkId(workOrderId);
                if (totalPartsCost != null && work.getEstimatedCost() != null) {
                        work.setActualCost(work.getEstimatedCost().add(totalPartsCost));
                }

                work = workRepository.save(work);
                log.info("Work order completed successfully");

                return mapToWorkOrderResponse(work);
        }

        @Override
        @Transactional
        public WorkOrderResponse cancelWorkOrder(Long workOrderId) {
                log.info("Cancelling work order with ID: {}", workOrderId);

                Work work = workRepository.findById(workOrderId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Work order not found with ID: " + workOrderId));

                WorkStatus cancelledStatus = workStatusRepository.findById(WorkOrderStatus.CANCELLED.getId())
                                .orElseThrow(() -> new IllegalArgumentException("Cancelled status not found"));

                work.setWorkStatus(cancelledStatus);
                work = workRepository.save(work);

                log.info("Work order cancelled successfully");
                return mapToWorkOrderResponse(work);
        }

        @Override
        @Transactional
        public WorkOrderResponse finishWithoutExecution(Long workOrderId, String reason) {
                log.info("Finishing work order without execution with ID: {}", workOrderId);

                Work work = workRepository.findById(workOrderId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Work order not found with ID: " + workOrderId));

                // Verificar que el trabajo no esté en progreso
                if (work.getWorkStatus().getId().equals(WorkOrderStatus.IN_PROGRESS.getId())) {
                        throw new IllegalStateException(
                                        "Cannot finish without execution a work order that is in progress");
                }

                WorkStatus finishedWithoutExecutionStatus = workStatusRepository
                                .findById(WorkOrderStatus.FINISHED_WITHOUT_EXECUTION.getId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Finished without execution status not found"));

                // Liberar empleado asignado para nueva asignación
                User previouslyAssignedEmployee = work.getAssignedEmployee();
                work.setAssignedEmployee(null);
                work.setWorkStatus(finishedWithoutExecutionStatus);
                work.setCompletedAt(LocalDateTime.now());
                work.setClientApproved(false);

                work = workRepository.save(work);

                // Registrar en progress el motivo del cierre sin ejecución
                if (reason != null && !reason.isEmpty()) {
                        WorkProgress closeProgress = WorkProgress.builder()
                                        .work(work)
                                        .user(getCurrentUser())
                                        .progressDescription("Trabajo finalizado sin ejecución")
                                        .observations("Motivo: " + reason)
                                        .build();
                        workProgressRepository.save(closeProgress);
                }

                log.info("Work order finished without execution successfully. Employee {} is now available for reassignment",
                                previouslyAssignedEmployee != null ? previouslyAssignedEmployee.getId() : "none");

                return mapToWorkOrderResponse(work);
        }

        @Override
        @Transactional
        public WorkOrderResponse approveWorkOrder(Long workOrderId) {
                log.info("Approving work order with ID: {}", workOrderId);

                Work work = workRepository.findById(workOrderId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Work order not found with ID: " + workOrderId));

                work.setClientApproved(true);
                work.setClientApprovedAt(LocalDateTime.now());

                work = workRepository.save(work);
                log.info("Work order approved successfully");

                return mapToWorkOrderResponse(work);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<WorkOrderResponse> getWorkOrdersByEmployee(Long employeeId, Pageable pageable) {
                return workRepository.findByAssignedEmployeeId(employeeId, pageable)
                                .map(this::mapToWorkOrderResponse);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<WorkOrderResponse> getWorkOrdersByClient(String clientCui, Pageable pageable) {
                return workRepository.findByVehicleOwnerCui(clientCui, pageable)
                                .map(this::mapToWorkOrderResponse);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<WorkOrderResponse> getWorkOrdersByStatus(Long statusId, Pageable pageable) {
                return workRepository.findByWorkStatusId(statusId, pageable)
                                .map(this::mapToWorkOrderResponse);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<WorkOrderResponse> getWorkOrdersByServiceType(Long serviceTypeId, Pageable pageable) {
                return workRepository.findByServiceTypeId(serviceTypeId, pageable)
                                .map(this::mapToWorkOrderResponse);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<WorkOrderResponse> getWorkOrdersByVehicle(Long vehicleId, Pageable pageable) {
                List<Work> works = workRepository.findByVehicleIdOrderByCreatedAtDesc(vehicleId);

                // Aplicar paginación manual a la lista
                int start = (int) pageable.getOffset();
                int end = Math.min((start + pageable.getPageSize()), works.size());

                List<Work> pagedWorks = works.subList(start, end);
                List<WorkOrderResponse> content = pagedWorks.stream()
                                .map(this::mapToWorkOrderResponse)
                                .toList();

                return new org.springframework.data.domain.PageImpl<>(content, pageable, works.size());
        }

        @Override
        @Transactional(readOnly = true)
        public Page<WorkOrderResponse> getWorkOrdersByPriority(Integer priorityLevel, Pageable pageable) {
                return workRepository.findByPriorityLevel(priorityLevel, pageable)
                                .map(this::mapToWorkOrderResponse);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<WorkOrderResponse> getWorkOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate,
                        Pageable pageable) {
                return workRepository.findByCreatedAtBetween(startDate, endDate, pageable)
                                .map(this::mapToWorkOrderResponse);
        }

        @Override
        @Transactional(readOnly = true)
        public List<WorkOrderResponse> getActiveWorkOrdersByEmployee(Long employeeId) {
                return workRepository.findActiveWorksByEmployeeId(employeeId)
                                .stream()
                                .map(this::mapToWorkOrderResponse)
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public Long countWorkOrdersByStatus(Long statusId) {
                return workRepository.countByWorkStatusId(statusId);
        }

        @Override
        @Transactional(readOnly = true)
        public Long countActiveWorkOrdersByEmployee(Long employeeId) {
                return workRepository.countActiveWorksByEmployeeId(employeeId);
        }

        @Override
        @Transactional
        public WorkProgressResponse addWorkProgress(WorkProgressRequest request) {
                log.info("Adding work progress for work ID: {}", request.getWorkId());

                Work work = workRepository.findById(request.getWorkId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Work order not found with ID: " + request.getWorkId()));

                User currentUser = getCurrentUser();

                WorkProgress workProgress = WorkProgress.builder()
                                .work(work)
                                .user(currentUser)
                                .progressDescription(request.getProgressDescription())
                                .hoursWorked(request.getHoursWorked())
                                .observations(request.getObservations())
                                .symptomsDetected(request.getSymptomsDetected())
                                .additionalDamageFound(request.getAdditionalDamageFound())
                                .build();

                workProgress = workProgressRepository.save(workProgress);
                log.info("Work progress added successfully");

                return mapToWorkProgressResponse(workProgress);
        }

        @Override
        @Transactional(readOnly = true)
        public List<WorkProgressResponse> getWorkProgressByWorkOrder(Long workOrderId) {
                return workProgressRepository.findByWorkIdOrderByRecordedAtAsc(workOrderId)
                                .stream()
                                .map(this::mapToWorkProgressResponse)
                                .toList();
        }

        @Override
        @Transactional
        public WorkPartResponse addWorkPart(WorkPartRequest request) {
                log.info("Adding work part for work ID: {}", request.getWorkId());

                Work work = workRepository.findById(request.getWorkId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Work order not found with ID: " + request.getWorkId()));

                Part part = partRepository.findById(request.getPartId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Part not found with ID: " + request.getPartId()));

                User currentUser = getCurrentUser();

                WorkPart workPart = WorkPart.builder()
                                .work(work)
                                .part(part)
                                .quantityNeeded(request.getQuantityNeeded())
                                .unitPrice(request.getUnitPrice())
                                .requestedBy(currentUser)
                                .build();

                workPart = workPartRepository.save(workPart);
                log.info("Work part added successfully");

                return mapToWorkPartResponse(workPart);
        }

        @Override
        @Transactional(readOnly = true)
        public List<WorkPartResponse> getWorkPartsByWorkOrder(Long workOrderId) {
                return workPartRepository.findByWorkIdWithDetails(workOrderId)
                                .stream()
                                .map(this::mapToWorkPartResponse)
                                .toList();
        }

        @Override
        @Transactional
        public QuotationResponse createQuotation(QuotationRequest request) {
                log.info("Creating quotation for work ID: {}", request.getWorkId());

                Work work = workRepository.findById(request.getWorkId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Work order not found with ID: " + request.getWorkId()));

                User currentUser = getCurrentUser();

                WorkOrderQuotation quotation = WorkOrderQuotation.builder()
                                .work(work)
                                .totalPartsCost(request.getTotalPartsCost())
                                .totalLaborCost(request.getTotalLaborCost())
                                .totalAmount(request.getTotalAmount())
                                .validUntil(request.getValidUntil())
                                .createdBy(currentUser)
                                .build();

                quotation = workOrderQuotationRepository.save(quotation);
                log.info("Quotation created successfully");

                return mapToQuotationResponse(quotation);
        }

        @Override
        @Transactional(readOnly = true)
        public List<QuotationResponse> getQuotationsByWorkOrder(Long workOrderId) {
                return workOrderQuotationRepository.findByWorkIdOrderByCreatedAtDesc(workOrderId)
                                .stream()
                                .map(this::mapToQuotationResponse)
                                .toList();
        }

        @Override
        @Transactional
        public QuotationResponse approveQuotation(Long quotationId) {
                log.info("Approving quotation with ID: {}", quotationId);

                WorkOrderQuotation quotation = workOrderQuotationRepository.findById(quotationId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Quotation not found with ID: " + quotationId));

                quotation.setClientApproved(true);
                quotation.setApprovedAt(LocalDateTime.now());

                quotation = workOrderQuotationRepository.save(quotation);
                log.info("Quotation approved successfully");

                return mapToQuotationResponse(quotation);
        }

        @Override
        @Transactional(readOnly = true)
        public List<ServiceTypeResponse> getAllServiceTypes() {
                return serviceTypeRepository.findAll()
                                .stream()
                                .map(this::mapToServiceTypeResponse)
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public List<WorkStatusResponse> getAllWorkStatuses() {
                return workStatusRepository.findAll()
                                .stream()
                                .map(this::mapToWorkStatusResponse)
                                .toList();
        }

        private User getCurrentUser() {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                return userRepository.findByUsername(username)
                                .orElseThrow(() -> new IllegalStateException("Current user not found"));
        }

        private UserResponse mapToUserResponse(User user) {
                return UserResponse.builder()
                                .id(user.getId())
                                .cui(user.getPerson().getCui())
                                .nit(user.getPerson().getNit())
                                .firstName(user.getPerson().getFirstName())
                                .lastName(user.getPerson().getLastName())
                                .email(user.getPerson().getEmail())
                                .phone(user.getPerson().getPhone())
                                .username(user.getUsername())
                                .userType(user.getUserType().getName())
                                .gender(user.getPerson().getGender() != null ? user.getPerson().getGender().getName()
                                                : null)
                                .isActive(user.getIsActive())
                                .lastLogin(user.getLastLogin())
                                .createdAt(user.getCreatedAt())
                                .build();
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

        private WorkProgressResponse mapToWorkProgressResponse(WorkProgress workProgress) {
                return WorkProgressResponse.builder()
                                .id(workProgress.getId())
                                .workId(workProgress.getWork().getId())
                                .progressDescription(workProgress.getProgressDescription())
                                .hoursWorked(workProgress.getHoursWorked())
                                .observations(workProgress.getObservations())
                                .symptomsDetected(workProgress.getSymptomsDetected())
                                .additionalDamageFound(workProgress.getAdditionalDamageFound())
                                .recordedAt(workProgress.getRecordedAt())
                                .build();
        }

        private WorkPartResponse mapToWorkPartResponse(WorkPart workPart) {
                BigDecimal totalPrice = workPart.getUnitPrice() != null && workPart.getQuantityUsed() != null
                                ? workPart.getUnitPrice().multiply(BigDecimal.valueOf(workPart.getQuantityUsed()))
                                : BigDecimal.ZERO;

                return WorkPartResponse.builder()
                                .id(workPart.getId())
                                .workId(workPart.getWork().getId())
                                .quantityNeeded(workPart.getQuantityNeeded())
                                .quantityUsed(workPart.getQuantityUsed())
                                .unitPrice(workPart.getUnitPrice())
                                .totalPrice(totalPrice)
                                .createdAt(workPart.getCreatedAt())
                                .build();
        }

        private QuotationResponse mapToQuotationResponse(WorkOrderQuotation quotation) {
                return QuotationResponse.builder()
                                .id(quotation.getId())
                                .workId(quotation.getWork().getId())
                                .totalPartsCost(quotation.getTotalPartsCost())
                                .totalLaborCost(quotation.getTotalLaborCost())
                                .totalAmount(quotation.getTotalAmount())
                                .validUntil(quotation.getValidUntil())
                                .clientApproved(quotation.getClientApproved())
                                .approvedAt(quotation.getApprovedAt())
                                .createdBy(mapToUserResponse(quotation.getCreatedBy()))
                                .createdAt(quotation.getCreatedAt())
                                .updatedAt(quotation.getUpdatedAt())
                                .build();
        }

        private ServiceTypeResponse mapToServiceTypeResponse(ServiceType serviceType) {
                return ServiceTypeResponse.builder()
                                .id(serviceType.getId())
                                .name(serviceType.getName())
                                .description(serviceType.getDescription())
                                .createdAt(serviceType.getCreatedAt())
                                .build();
        }

        private WorkStatusResponse mapToWorkStatusResponse(WorkStatus workStatus) {
                return WorkStatusResponse.builder()
                                .id(workStatus.getId())
                                .name(workStatus.getName())
                                .description(workStatus.getDescription())
                                .createdAt(workStatus.getCreatedAt())
                                .build();
        }

        // private void validateStateTransition(WorkStatus from, WorkStatus to) {
        // // Lógica de validación de transiciones válidas
        // Map<String, List<String>> validTransitions = Map.of(
        // "PENDIENTE", List.of("EN_PROGRESO", "CANCELADO"),
        // "EN_PROGRESO", List.of("COMPLETADO", "CANCELADO"),
        // "COMPLETADO", List.of());

        // if (!validTransitions.get(from.getName()).contains(to.getName())) {
        // throw new IllegalStateException(
        // "Invalid state transition from " + from.getName() + " to " + to.getName());
        // }
        // }
}