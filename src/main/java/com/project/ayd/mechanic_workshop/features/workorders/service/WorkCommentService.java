package com.project.ayd.mechanic_workshop.features.workorders.service;

import com.project.ayd.mechanic_workshop.features.workorders.dto.WorkCommentRequest;
import com.project.ayd.mechanic_workshop.features.workorders.dto.WorkCommentResponse;
import com.project.ayd.mechanic_workshop.features.workorders.dto.WorkCommentResponseRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface WorkCommentService {

    WorkCommentResponse addComment(WorkCommentRequest request);

    WorkCommentResponse getCommentById(Long commentId);

    List<WorkCommentResponse> getCommentsByWorkId(Long workId);

    Page<WorkCommentResponse> getCommentsByUser(Long userId, Pageable pageable);

    Page<WorkCommentResponse> getCommentsByAssignedEmployee(Long employeeId, Pageable pageable);

    List<WorkCommentResponse> getCommentsByType(String commentType);

    List<WorkCommentResponse> getUrgentUnrespondedComments();

    List<WorkCommentResponse> getCommentsRequiringResponse();

    Page<WorkCommentResponse> getCommentsByClient(String clientCui, Pageable pageable);

    WorkCommentResponse respondToComment(Long commentId, WorkCommentResponseRequest request);

    List<WorkCommentResponse> getCommentsByWorkAndType(Long workId, String commentType);

    Map<String, Long> getCommentStatistics();

    Long getCommentCountByWork(Long workId);

    void deleteComment(Long commentId);
}