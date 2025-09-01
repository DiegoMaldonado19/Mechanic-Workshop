package com.project.ayd.mechanic_workshop.features.workorders.service;

import com.project.ayd.mechanic_workshop.features.auth.entity.User;
import com.project.ayd.mechanic_workshop.features.auth.repository.UserRepository;
import com.project.ayd.mechanic_workshop.features.users.dto.SpecializationTypeResponse;
import com.project.ayd.mechanic_workshop.features.users.dto.UserResponse;
import com.project.ayd.mechanic_workshop.features.users.entity.SpecializationType;
import com.project.ayd.mechanic_workshop.features.users.repository.SpecializationTypeRepository;
import com.project.ayd.mechanic_workshop.features.workorders.dto.UpdateWorkSupportRequest;
import com.project.ayd.mechanic_workshop.features.workorders.dto.WorkSupportRequest;
import com.project.ayd.mechanic_workshop.features.workorders.dto.WorkSupportResponse;
import com.project.ayd.mechanic_workshop.features.workorders.entity.Work;
import com.project.ayd.mechanic_workshop.features.workorders.entity.WorkSupport;
import com.project.ayd.mechanic_workshop.features.workorders.enums.Priority;
import com.project.ayd.mechanic_workshop.features.workorders.repository.WorkRepository;
import com.project.ayd.mechanic_workshop.features.workorders.repository.WorkSupportRepository;
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
public class WorkSupportServiceImpl implements WorkSupportService {

    private final WorkSupportRepository workSupportRepository;
    private final WorkRepository workRepository;
    private final UserRepository userRepository;
    private final SpecializationTypeRepository specializationTypeRepository;

    @Override
    @Transactional
    public WorkSupportResponse requestSupport(WorkSupportRequest request) {
        log.info("Requesting support for work ID: {}", request.getWorkId());

        Work work = workRepository.findById(request.getWorkId())
                .orElseThrow(
                        () -> new IllegalArgumentException("Work order not found with ID: " + request.getWorkId()));

        User currentUser = getCurrentUser();

        // Verificar que el usuario actual es el empleado asignado al trabajo
        if (work.getAssignedEmployee() == null || !work.getAssignedEmployee().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("You can only request support for work orders assigned to you");
        }

        SpecializationType specializationType = null;
        if (request.getSpecializationNeededId() != null) {
            specializationType = specializationTypeRepository.findById(request.getSpecializationNeededId())
                    .orElseThrow(() -> new IllegalArgumentException("Specialization type not found"));
        }

        WorkSupport workSupport = WorkSupport.builder()
                .work(work)
                .requestedBy(currentUser)
                .specializationNeeded(specializationType)
                .reason(request.getReason())
                .urgencyLevel(request.getUrgencyLevel())
                .status(WorkSupport.SupportStatus.Pendiente)
                .build();

        // Si se especifica un especialista, asignarlo directamente
        if (request.getAssignedSpecialistId() != null) {
            User specialist = userRepository.findById(request.getAssignedSpecialistId())
                    .orElseThrow(() -> new IllegalArgumentException("Specialist not found"));

            workSupport.setAssignedSpecialist(specialist);
            workSupport.setStatus(WorkSupport.SupportStatus.Asignado);
            workSupport.setAssignedAt(LocalDateTime.now());
        }

        workSupport = workSupportRepository.save(workSupport);
        log.info("Support request created successfully with ID: {}", workSupport.getId());

        return mapToWorkSupportResponse(workSupport);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkSupportResponse getSupportById(Long supportId) {
        WorkSupport workSupport = workSupportRepository.findByIdWithDetails(supportId)
                .orElseThrow(() -> new IllegalArgumentException("Support request not found with ID: " + supportId));
        return mapToWorkSupportResponse(workSupport);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkSupportResponse> getSupportByWorkId(Long workId) {
        return workSupportRepository.findByWorkIdOrderByCreatedAtDesc(workId)
                .stream()
                .map(this::mapToWorkSupportResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WorkSupportResponse> getSupportRequestsByEmployee(Long employeeId, Pageable pageable) {
        return workSupportRepository.findByRequestedByIdOrderByCreatedAtDesc(employeeId, pageable)
                .map(this::mapToWorkSupportResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WorkSupportResponse> getSupportRequestsForSpecialist(Long specialistId, Pageable pageable) {
        return workSupportRepository.findByAssignedSpecialistIdOrderByCreatedAtDesc(specialistId, pageable)
                .map(this::mapToWorkSupportResponse);
    }

    @Override
    @Transactional
    public WorkSupportResponse assignSpecialist(Long supportId, Long specialistId) {
        log.info("Assigning specialist {} to support request {}", specialistId, supportId);

        WorkSupport workSupport = workSupportRepository.findById(supportId)
                .orElseThrow(() -> new IllegalArgumentException("Support request not found"));

        if (workSupport.getStatus() != WorkSupport.SupportStatus.Pendiente) {
            throw new IllegalStateException("Support request must be pending to assign specialist");
        }

        User specialist = userRepository.findById(specialistId)
                .orElseThrow(() -> new IllegalArgumentException("Specialist not found"));

        workSupport.setAssignedSpecialist(specialist);
        workSupport.setStatus(WorkSupport.SupportStatus.Asignado);
        workSupport.setAssignedAt(LocalDateTime.now());

        workSupport = workSupportRepository.save(workSupport);
        log.info("Specialist assigned successfully");

        return mapToWorkSupportResponse(workSupport);
    }

    @Override
    @Transactional
    public WorkSupportResponse updateSupport(Long supportId, UpdateWorkSupportRequest request) {
        log.info("Updating support request with ID: {}", supportId);

        WorkSupport workSupport = workSupportRepository.findById(supportId)
                .orElseThrow(() -> new IllegalArgumentException("Support request not found"));

        if (request.getAssignedSpecialistId() != null) {
            User specialist = userRepository.findById(request.getAssignedSpecialistId())
                    .orElseThrow(() -> new IllegalArgumentException("Specialist not found"));
            workSupport.setAssignedSpecialist(specialist);
            if (workSupport.getStatus() == WorkSupport.SupportStatus.Pendiente) {
                workSupport.setStatus(WorkSupport.SupportStatus.Asignado);
                workSupport.setAssignedAt(LocalDateTime.now());
            }
        }

        if (request.getSpecialistNotes() != null) {
            workSupport.setSpecialistNotes(request.getSpecialistNotes());
        }

        if (request.getResolutionNotes() != null) {
            workSupport.setResolutionNotes(request.getResolutionNotes());
        }

        if (request.getStatus() != null) {
            WorkSupport.SupportStatus newStatus = WorkSupport.SupportStatus.valueOf(request.getStatus());
            workSupport.setStatus(newStatus);

            if (newStatus == WorkSupport.SupportStatus.Completado) {
                workSupport.setCompletedAt(LocalDateTime.now());
            }
        }

        workSupport = workSupportRepository.save(workSupport);
        log.info("Support request updated successfully");

        return mapToWorkSupportResponse(workSupport);
    }

    @Override
    @Transactional
    public WorkSupportResponse startSupport(Long supportId) {
        log.info("Starting support request with ID: {}", supportId);

        WorkSupport workSupport = workSupportRepository.findById(supportId)
                .orElseThrow(() -> new IllegalArgumentException("Support request not found"));

        if (workSupport.getStatus() != WorkSupport.SupportStatus.Asignado) {
            throw new IllegalStateException("Support request must be assigned before starting");
        }

        workSupport.setStatus(WorkSupport.SupportStatus.En_Progreso);
        workSupport = workSupportRepository.save(workSupport);

        log.info("Support request started successfully");
        return mapToWorkSupportResponse(workSupport);
    }

    @Override
    @Transactional
    public WorkSupportResponse completeSupport(Long supportId) {
        log.info("Completing support request with ID: {}", supportId);

        WorkSupport workSupport = workSupportRepository.findById(supportId)
                .orElseThrow(() -> new IllegalArgumentException("Support request not found"));

        workSupport.setStatus(WorkSupport.SupportStatus.Completado);
        workSupport.setCompletedAt(LocalDateTime.now());

        workSupport = workSupportRepository.save(workSupport);
        log.info("Support request completed successfully");

        return mapToWorkSupportResponse(workSupport);
    }

    @Override
    @Transactional
    public WorkSupportResponse cancelSupport(Long supportId) {
        log.info("Cancelling support request with ID: {}", supportId);

        WorkSupport workSupport = workSupportRepository.findById(supportId)
                .orElseThrow(() -> new IllegalArgumentException("Support request not found"));

        workSupport.setStatus(WorkSupport.SupportStatus.Cancelado);
        workSupport = workSupportRepository.save(workSupport);

        log.info("Support request cancelled successfully");
        return mapToWorkSupportResponse(workSupport);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WorkSupportResponse> getSupportByStatus(String status, Pageable pageable) {
        WorkSupport.SupportStatus supportStatus = WorkSupport.SupportStatus.valueOf(status);
        return workSupportRepository.findByStatusOrderByUrgencyAndCreated(supportStatus, pageable)
                .map(this::mapToWorkSupportResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkSupportResponse> getPendingSupportBySpecialization(Long specializationId) {
        return workSupportRepository.findPendingSupportBySpecialization(specializationId)
                .stream()
                .map(this::mapToWorkSupportResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkSupportResponse> getUnassignedPendingSupports() {
        return workSupportRepository.findUnassignedPendingSupports()
                .stream()
                .map(this::mapToWorkSupportResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getSupportStatistics() {
        Map<String, Long> statistics = new HashMap<>();
        for (WorkSupport.SupportStatus status : WorkSupport.SupportStatus.values()) {
            Long count = workSupportRepository.countByStatus(status);
            statistics.put(status.name(), count);
        }
        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getActiveSupportCountForSpecialist(Long specialistId) {
        return workSupportRepository.countActiveSupportsForSpecialist(specialistId);
    }

    @Override
    @Transactional
    public WorkSupportResponse findBestSpecialistForSupport(Long supportId) {
        log.info("Finding best specialist for support ID: {}", supportId);

        WorkSupport workSupport = workSupportRepository.findById(supportId)
                .orElseThrow(() -> new IllegalArgumentException("Support request not found"));

        if (workSupport.getStatus() != WorkSupport.SupportStatus.Pendiente) {
            throw new IllegalStateException("Support request must be pending to auto-assign");
        }

        // Buscar especialistas disponibles (lógica simplificada)
        List<User> availableSpecialists = userRepository.findAll().stream()
                .filter(user -> "Especialista".equals(user.getUserType().getName()))
                .toList();

        if (availableSpecialists.isEmpty()) {
            throw new IllegalStateException("No specialists available for assignment");
        }

        // Asignar al primer especialista disponible (se puede mejorar con lógica más
        // compleja)
        User selectedSpecialist = availableSpecialists.get(0);

        return assignSpecialist(supportId, selectedSpecialist.getId());
    }

    @Override
    @Transactional
    public void deleteSupport(Long supportId) {
        log.info("Deleting support request with ID: {}", supportId);

        WorkSupport workSupport = workSupportRepository.findById(supportId)
                .orElseThrow(() -> new IllegalArgumentException("Support request not found"));

        workSupportRepository.delete(workSupport);
        log.info("Support request deleted successfully");
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Current user not found"));
    }

    private WorkSupportResponse mapToWorkSupportResponse(WorkSupport workSupport) {
        String urgencyDisplayName = workSupport.getUrgencyLevel() != null
                ? Priority.fromLevel(workSupport.getUrgencyLevel()).getDisplayName()
                : null;

        UserResponse requestedByResponse = mapToUserResponse(workSupport.getRequestedBy());
        UserResponse assignedSpecialistResponse = workSupport.getAssignedSpecialist() != null
                ? mapToUserResponse(workSupport.getAssignedSpecialist())
                : null;

        SpecializationTypeResponse specializationResponse = workSupport.getSpecializationNeeded() != null
                ? mapToSpecializationTypeResponse(workSupport.getSpecializationNeeded())
                : null;

        return WorkSupportResponse.builder()
                .id(workSupport.getId())
                .workId(workSupport.getWork().getId())
                .workDescription(workSupport.getWork().getProblemDescription())
                .vehicleLicensePlate(workSupport.getWork().getVehicle().getLicensePlate())
                .requestedBy(requestedByResponse)
                .assignedSpecialist(assignedSpecialistResponse)
                .specializationNeeded(specializationResponse)
                .reason(workSupport.getReason())
                .urgencyLevel(workSupport.getUrgencyLevel())
                .urgencyDisplayName(urgencyDisplayName)
                .status(workSupport.getStatus().name())
                .specialistNotes(workSupport.getSpecialistNotes())
                .resolutionNotes(workSupport.getResolutionNotes())
                .assignedAt(workSupport.getAssignedAt())
                .completedAt(workSupport.getCompletedAt())
                .createdAt(workSupport.getCreatedAt())
                .updatedAt(workSupport.getUpdatedAt())
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

    private SpecializationTypeResponse mapToSpecializationTypeResponse(SpecializationType specialization) {
        return SpecializationTypeResponse.builder()
                .id(specialization.getId())
                .name(specialization.getName())
                .description(specialization.getDescription())
                .build();
    }
}