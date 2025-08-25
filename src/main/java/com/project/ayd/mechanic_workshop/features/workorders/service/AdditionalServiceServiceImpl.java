package com.project.ayd.mechanic_workshop.features.workorders.service;

import com.project.ayd.mechanic_workshop.features.auth.entity.User;
import com.project.ayd.mechanic_workshop.features.auth.repository.UserRepository;
import com.project.ayd.mechanic_workshop.features.users.dto.UserResponse;
import com.project.ayd.mechanic_workshop.features.workorders.dto.AdditionalServiceRequestDto;
import com.project.ayd.mechanic_workshop.features.workorders.dto.AdditionalServiceResponse;
import com.project.ayd.mechanic_workshop.features.workorders.dto.ApproveAdditionalServiceRequest;
import com.project.ayd.mechanic_workshop.features.workorders.dto.ServiceTypeResponse;
import com.project.ayd.mechanic_workshop.features.workorders.entity.AdditionalServiceRequest;
import com.project.ayd.mechanic_workshop.features.workorders.entity.ServiceType;
import com.project.ayd.mechanic_workshop.features.workorders.entity.Work;
import com.project.ayd.mechanic_workshop.features.workorders.enums.Priority;
import com.project.ayd.mechanic_workshop.features.workorders.repository.AdditionalServiceRequestRepository;
import com.project.ayd.mechanic_workshop.features.workorders.repository.ServiceTypeRepository;
import com.project.ayd.mechanic_workshop.features.workorders.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdditionalServiceServiceImpl implements AdditionalServiceService {

    private final AdditionalServiceRequestRepository additionalServiceRequestRepository;
    private final WorkRepository workRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AdditionalServiceResponse requestAdditionalService(AdditionalServiceRequestDto request) {
        log.info("Requesting additional service for work ID: {}", request.getWorkId());

        Work work = workRepository.findByIdWithDetails(request.getWorkId())
                .orElseThrow(
                        () -> new IllegalArgumentException("Work order not found with ID: " + request.getWorkId()));

        ServiceType serviceType = serviceTypeRepository.findById(request.getServiceTypeId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Service type not found with ID: " + request.getServiceTypeId()));

        User currentUser = getCurrentUser();

        // Verificar permisos: solo empleados asignados o administradores pueden
        // solicitar servicios adicionales
        boolean canRequest = "ADMINISTRADOR".equals(currentUser.getUserType().getName()) ||
                (work.getAssignedEmployee() != null && work.getAssignedEmployee().getId().equals(currentUser.getId()));

        if (!canRequest) {
            throw new IllegalStateException(
                    "Only assigned employees or administrators can request additional services");
        }

        AdditionalServiceRequest additionalServiceRequest = AdditionalServiceRequest.builder()
                .work(work)
                .serviceType(serviceType)
                .requestedBy(currentUser)
                .description(request.getDescription())
                .justification(request.getJustification())
                .estimatedHours(request.getEstimatedHours())
                .estimatedCost(request.getEstimatedCost())
                .urgencyLevel(request.getUrgencyLevel())
                .status(AdditionalServiceRequest.RequestStatus.PENDING_APPROVAL)
                .build();

        additionalServiceRequest = additionalServiceRequestRepository.save(additionalServiceRequest);
        log.info("Additional service request created successfully with ID: {}", additionalServiceRequest.getId());

        return mapToAdditionalServiceResponse(additionalServiceRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public AdditionalServiceResponse getAdditionalServiceById(Long requestId) {
        AdditionalServiceRequest request = additionalServiceRequestRepository.findByIdWithDetails(requestId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Additional service request not found with ID: " + requestId));
        return mapToAdditionalServiceResponse(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdditionalServiceResponse> getAdditionalServicesByWork(Long workId) {
        return additionalServiceRequestRepository.findByWorkIdOrderByCreatedAtDesc(workId)
                .stream()
                .map(this::mapToAdditionalServiceResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdditionalServiceResponse> getAdditionalServicesByEmployee(Long employeeId, Pageable pageable) {
        return additionalServiceRequestRepository.findByRequestedByIdOrderByCreatedAtDesc(employeeId, pageable)
                .map(this::mapToAdditionalServiceResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdditionalServiceResponse> getAdditionalServicesByClient(String clientCui, Pageable pageable) {
        return additionalServiceRequestRepository.findByClientCuiOrderByCreatedAtDesc(clientCui, pageable)
                .map(this::mapToAdditionalServiceResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdditionalServiceResponse> getAdditionalServicesByStatus(String status, Pageable pageable) {
        AdditionalServiceRequest.RequestStatus requestStatus = AdditionalServiceRequest.RequestStatus.valueOf(status);
        return additionalServiceRequestRepository.findByStatusOrderByUrgencyAndCreated(requestStatus, pageable)
                .map(this::mapToAdditionalServiceResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdditionalServiceResponse> getPendingClientApproval(String clientCui) {
        return additionalServiceRequestRepository.findPendingApprovalByClient(clientCui)
                .stream()
                .map(this::mapToAdditionalServiceResponse)
                .toList();
    }

    @Override
    @Transactional
    public AdditionalServiceResponse approveOrRejectService(Long requestId, ApproveAdditionalServiceRequest request) {
        log.info("Processing approval/rejection for additional service request ID: {}", requestId);

        AdditionalServiceRequest serviceRequest = additionalServiceRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Additional service request not found"));

        if (serviceRequest.getStatus() != AdditionalServiceRequest.RequestStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Request must be pending approval to be processed");
        }

        User currentUser = getCurrentUser();

        if ("APPROVE".equalsIgnoreCase(request.getAction())) {
            serviceRequest.setStatus(AdditionalServiceRequest.RequestStatus.APPROVED);
            serviceRequest.setApprovedBy(currentUser);
            serviceRequest.setApprovalNotes(request.getNotes());
            log.info("Additional service request approved");
        } else if ("REJECT".equalsIgnoreCase(request.getAction())) {
            serviceRequest.setStatus(AdditionalServiceRequest.RequestStatus.REJECTED);
            serviceRequest.setApprovedBy(currentUser);
            serviceRequest.setRejectionReason(request.getRejectionReason());
            log.info("Additional service request rejected");
        } else {
            throw new IllegalArgumentException("Invalid action. Must be APPROVE or REJECT");
        }

        serviceRequest = additionalServiceRequestRepository.save(serviceRequest);
        return mapToAdditionalServiceResponse(serviceRequest);
    }

    @Override
    @Transactional
    public AdditionalServiceResponse clientApproveService(Long requestId) {
        log.info("Client approving additional service request ID: {}", requestId);

        AdditionalServiceRequest serviceRequest = additionalServiceRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Additional service request not found"));

        if (serviceRequest.getStatus() != AdditionalServiceRequest.RequestStatus.APPROVED) {
            throw new IllegalStateException("Service must be approved by administrator before client can approve");
        }

        String currentUserCui = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!serviceRequest.getWork().getVehicle().getOwnerCui().equals(currentUserCui)) {
            throw new IllegalStateException("You can only approve services for your own vehicles");
        }

        serviceRequest.setClientApproved(true);
        serviceRequest.setClientApprovedAt(LocalDateTime.now());
        serviceRequest.setStatus(AdditionalServiceRequest.RequestStatus.IN_PROGRESS);

        serviceRequest = additionalServiceRequestRepository.save(serviceRequest);
        log.info("Additional service approved by client");

        return mapToAdditionalServiceResponse(serviceRequest);
    }

    @Override
    @Transactional
    public AdditionalServiceResponse clientRejectService(Long requestId, String reason) {
        log.info("Client rejecting additional service request ID: {}", requestId);

        AdditionalServiceRequest serviceRequest = additionalServiceRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Additional service request not found"));

        String currentUserCui = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!serviceRequest.getWork().getVehicle().getOwnerCui().equals(currentUserCui)) {
            throw new IllegalStateException("You can only reject services for your own vehicles");
        }

        serviceRequest.setClientApproved(false);
        serviceRequest.setStatus(AdditionalServiceRequest.RequestStatus.REJECTED);
        serviceRequest.setRejectionReason(reason);

        serviceRequest = additionalServiceRequestRepository.save(serviceRequest);
        log.info("Additional service rejected by client");

        return mapToAdditionalServiceResponse(serviceRequest);
    }

    @Override
    @Transactional
    public AdditionalServiceResponse startAdditionalService(Long requestId) {
        log.info("Starting additional service request ID: {}", requestId);

        AdditionalServiceRequest serviceRequest = additionalServiceRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Additional service request not found"));

        if (!serviceRequest.getClientApproved()) {
            throw new IllegalStateException("Service must be approved by client before starting");
        }

        serviceRequest.setStatus(AdditionalServiceRequest.RequestStatus.IN_PROGRESS);
        serviceRequest = additionalServiceRequestRepository.save(serviceRequest);

        log.info("Additional service started");
        return mapToAdditionalServiceResponse(serviceRequest);
    }

    @Override
    @Transactional
    public AdditionalServiceResponse completeAdditionalService(Long requestId) {
        log.info("Completing additional service request ID: {}", requestId);

        AdditionalServiceRequest serviceRequest = additionalServiceRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Additional service request not found"));

        if (serviceRequest.getStatus() != AdditionalServiceRequest.RequestStatus.IN_PROGRESS) {
            throw new IllegalStateException("Service must be in progress to be completed");
        }

        serviceRequest.setStatus(AdditionalServiceRequest.RequestStatus.COMPLETED);
        serviceRequest = additionalServiceRequestRepository.save(serviceRequest);

        log.info("Additional service completed");
        return mapToAdditionalServiceResponse(serviceRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdditionalServiceResponse> getApprovedButNotClientApproved() {
        return additionalServiceRequestRepository.findApprovedButNotClientApproved()
                .stream()
                .map(this::mapToAdditionalServiceResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getAdditionalServiceStatistics() {
        Map<String, Long> statistics = new HashMap<>();

        for (AdditionalServiceRequest.RequestStatus status : AdditionalServiceRequest.RequestStatus.values()) {
            Long count = additionalServiceRequestRepository.countByStatus(status);
            statistics.put(status.name(), count);
        }

        statistics.put("totalRequests", additionalServiceRequestRepository.count());

        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getAdditionalServiceCountByWork(Long workId) {
        return additionalServiceRequestRepository.countByWorkId(workId);
    }

    @Override
    @Transactional
    public void deleteAdditionalServiceRequest(Long requestId) {
        log.info("Deleting additional service request with ID: {}", requestId);

        AdditionalServiceRequest serviceRequest = additionalServiceRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Additional service request not found"));

        additionalServiceRequestRepository.delete(serviceRequest);
        log.info("Additional service request deleted successfully");
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Current user not found"));
    }

    private AdditionalServiceResponse mapToAdditionalServiceResponse(AdditionalServiceRequest request) {
        String urgencyDisplayName = request.getUrgencyLevel() != null
                ? Priority.fromLevel(request.getUrgencyLevel()).getDisplayName()
                : null;

        UserResponse requestedByResponse = mapToUserResponse(request.getRequestedBy());
        UserResponse approvedByResponse = request.getApprovedBy() != null ? mapToUserResponse(request.getApprovedBy())
                : null;

        ServiceTypeResponse serviceTypeResponse = mapToServiceTypeResponse(request.getServiceType());

        return AdditionalServiceResponse.builder()
                .id(request.getId())
                .workId(request.getWork().getId())
                .workDescription(request.getWork().getProblemDescription())
                .vehicleLicensePlate(request.getWork().getVehicle().getLicensePlate())
                .serviceType(serviceTypeResponse)
                .requestedBy(requestedByResponse)
                .description(request.getDescription())
                .justification(request.getJustification())
                .estimatedHours(request.getEstimatedHours())
                .estimatedCost(request.getEstimatedCost())
                .urgencyLevel(request.getUrgencyLevel())
                .urgencyDisplayName(urgencyDisplayName)
                .status(request.getStatus().name())
                .clientApproved(request.getClientApproved())
                .clientApprovedAt(request.getClientApprovedAt())
                .approvedBy(approvedByResponse)
                .approvalNotes(request.getApprovalNotes())
                .rejectionReason(request.getRejectionReason())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getPerson() != null ? user.getPerson().getFirstName() : null)
                .lastName(user.getPerson() != null ? user.getPerson().getLastName() : null)
                .email(user.getPerson() != null ? user.getPerson().getEmail() : null)
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
}