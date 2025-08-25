package com.project.ayd.mechanic_workshop.features.workorders.service;

import com.project.ayd.mechanic_workshop.features.auth.entity.User;
import com.project.ayd.mechanic_workshop.features.auth.repository.UserRepository;
import com.project.ayd.mechanic_workshop.features.users.dto.UserResponse;
import com.project.ayd.mechanic_workshop.features.workorders.dto.WorkCommentRequest;
import com.project.ayd.mechanic_workshop.features.workorders.dto.WorkCommentResponse;
import com.project.ayd.mechanic_workshop.features.workorders.dto.WorkCommentResponseRequest;
import com.project.ayd.mechanic_workshop.features.workorders.entity.Work;
import com.project.ayd.mechanic_workshop.features.workorders.entity.WorkComment;
import com.project.ayd.mechanic_workshop.features.workorders.repository.WorkCommentRepository;
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
public class WorkCommentServiceImpl implements WorkCommentService {

    private final WorkCommentRepository workCommentRepository;
    private final WorkRepository workRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public WorkCommentResponse addComment(WorkCommentRequest request) {
        log.info("Adding comment to work ID: {}", request.getWorkId());

        Work work = workRepository.findByIdWithDetails(request.getWorkId())
                .orElseThrow(
                        () -> new IllegalArgumentException("Work order not found with ID: " + request.getWorkId()));

        User currentUser = getCurrentUser();

        // Verificar permisos: cliente puede comentar solo en sus vehículos
        if ("CLIENTE".equals(currentUser.getUserType().getName())) {
            if (!work.getVehicle().getOwnerCui().equals(currentUser.getPerson().getCui())) {
                throw new IllegalStateException("You can only comment on work orders for your own vehicles");
            }
        }

        WorkComment.CommentType commentType = WorkComment.CommentType.valueOf(request.getCommentType());

        WorkComment workComment = WorkComment.builder()
                .work(work)
                .user(currentUser)
                .comment(request.getComment())
                .commentType(commentType)
                .isUrgent(request.getIsUrgent())
                .requiresResponse(request.getRequiresResponse())
                .build();

        workComment = workCommentRepository.save(workComment);
        log.info("Comment added successfully with ID: {}", workComment.getId());

        return mapToWorkCommentResponse(workComment);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkCommentResponse getCommentById(Long commentId) {
        WorkComment workComment = workCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found with ID: " + commentId));
        return mapToWorkCommentResponse(workComment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkCommentResponse> getCommentsByWorkId(Long workId) {
        return workCommentRepository.findByWorkIdOrderByCreatedAtAsc(workId)
                .stream()
                .map(this::mapToWorkCommentResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WorkCommentResponse> getCommentsByUser(Long userId, Pageable pageable) {
        return workCommentRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::mapToWorkCommentResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WorkCommentResponse> getCommentsByAssignedEmployee(Long employeeId, Pageable pageable) {
        return workCommentRepository.findCommentsByAssignedEmployeeOrderByCreatedAtDesc(employeeId, pageable)
                .map(this::mapToWorkCommentResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkCommentResponse> getCommentsByType(String commentType) {
        WorkComment.CommentType type = WorkComment.CommentType.valueOf(commentType);
        return workCommentRepository.findByCommentTypeOrderByCreatedAtDesc(type)
                .stream()
                .map(this::mapToWorkCommentResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkCommentResponse> getUrgentUnrespondedComments() {
        return workCommentRepository.findUrgentUnrespondedComments()
                .stream()
                .map(this::mapToWorkCommentResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkCommentResponse> getCommentsRequiringResponse() {
        return workCommentRepository.findCommentsRequiringResponse()
                .stream()
                .map(this::mapToWorkCommentResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WorkCommentResponse> getCommentsByClient(String clientCui, Pageable pageable) {
        return workCommentRepository.findByClientCuiOrderByCreatedAtDesc(clientCui, pageable)
                .map(this::mapToWorkCommentResponse);
    }

    @Override
    @Transactional
    public WorkCommentResponse respondToComment(Long commentId, WorkCommentResponseRequest request) {
        log.info("Responding to comment with ID: {}", commentId);

        WorkComment workComment = workCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found with ID: " + commentId));

        if (workComment.getResponse() != null) {
            throw new IllegalStateException("Comment has already been responded to");
        }

        User currentUser = getCurrentUser();

        workComment.setResponse(request.getResponse());
        workComment.setRespondedBy(currentUser);
        workComment.setRespondedAt(LocalDateTime.now());

        workComment = workCommentRepository.save(workComment);
        log.info("Comment response added successfully");

        return mapToWorkCommentResponse(workComment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkCommentResponse> getCommentsByWorkAndType(Long workId, String commentType) {
        WorkComment.CommentType type = WorkComment.CommentType.valueOf(commentType);
        return workCommentRepository.findByWorkIdAndCommentTypeOrderByCreatedAtDesc(workId, type)
                .stream()
                .map(this::mapToWorkCommentResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getCommentStatistics() {
        Map<String, Long> statistics = new HashMap<>();

        statistics.put("totalComments", workCommentRepository.count());
        statistics.put("pendingResponses", workCommentRepository.countPendingResponses());
        statistics.put("urgentPendingResponses", workCommentRepository.countUrgentPendingResponses());

        for (WorkComment.CommentType type : WorkComment.CommentType.values()) {
            Long count = workCommentRepository.findByCommentTypeOrderByCreatedAtDesc(type).size();
            statistics.put(type.name().toLowerCase() + "Comments", count);
        }

        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getCommentCountByWork(Long workId) {
        return workCommentRepository.countByWorkId(workId);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        log.info("Deleting comment with ID: {}", commentId);

        WorkComment workComment = workCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found with ID: " + commentId));

        workCommentRepository.delete(workComment);
        log.info("Comment deleted successfully");
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Current user not found"));
    }

    private WorkCommentResponse mapToWorkCommentResponse(WorkComment workComment) {
        UserResponse userResponse = mapToUserResponse(workComment.getUser());
        UserResponse respondedByResponse = workComment.getRespondedBy() != null
                ? mapToUserResponse(workComment.getRespondedBy())
                : null;

        return WorkCommentResponse.builder()
                .id(workComment.getId())
                .workId(workComment.getWork().getId())
                .workDescription(workComment.getWork().getProblemDescription())
                .vehicleLicensePlate(workComment.getWork().getVehicle().getLicensePlate())
                .user(userResponse)
                .comment(workComment.getComment())
                .commentType(workComment.getCommentType().name())
                .isUrgent(workComment.getIsUrgent())
                .requiresResponse(workComment.getRequiresResponse())
                .response(workComment.getResponse())
                .respondedBy(respondedByResponse)
                .respondedAt(workComment.getRespondedAt())
                .createdAt(workComment.getCreatedAt())
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
}