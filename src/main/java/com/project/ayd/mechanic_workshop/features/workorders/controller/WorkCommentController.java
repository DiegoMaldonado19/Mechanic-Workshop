package com.project.ayd.mechanic_workshop.features.workorders.controller;

import com.project.ayd.mechanic_workshop.features.workorders.dto.WorkCommentRequest;
import com.project.ayd.mechanic_workshop.features.workorders.dto.WorkCommentResponse;
import com.project.ayd.mechanic_workshop.features.workorders.dto.WorkCommentResponseRequest;
import com.project.ayd.mechanic_workshop.features.workorders.service.WorkCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/workorders/comments")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO', 'ESPECIALISTA', 'CLIENTE')")
public class WorkCommentController {

    private final WorkCommentService workCommentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CLIENTE')")
    public ResponseEntity<WorkCommentResponse> addComment(@Valid @RequestBody WorkCommentRequest request) {
        WorkCommentResponse response = workCommentService.addComment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<WorkCommentResponse> getCommentById(@PathVariable Long commentId) {
        WorkCommentResponse response = workCommentService.getCommentById(commentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/work/{workId}")
    public ResponseEntity<List<WorkCommentResponse>> getCommentsByWorkId(@PathVariable Long workId) {
        List<WorkCommentResponse> response = workCommentService.getCommentsByWorkId(workId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or #userId == authentication.principal.id")
    public ResponseEntity<Page<WorkCommentResponse>> getCommentsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<WorkCommentResponse> response = workCommentService.getCommentsByUser(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-comments")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Page<WorkCommentResponse>> getMyComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String currentUserCui = SecurityContextHolder.getContext().getAuthentication().getName();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<WorkCommentResponse> response = workCommentService.getCommentsByClient(currentUserCui, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or (#employeeId == authentication.principal.id and hasAnyRole('EMPLEADO', 'ESPECIALISTA'))")
    public ResponseEntity<Page<WorkCommentResponse>> getCommentsByAssignedEmployee(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<WorkCommentResponse> response = workCommentService.getCommentsByAssignedEmployee(employeeId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{commentType}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<WorkCommentResponse>> getCommentsByType(@PathVariable String commentType) {
        List<WorkCommentResponse> response = workCommentService.getCommentsByType(commentType);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/urgent/unresponded")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO', 'ESPECIALISTA')")
    public ResponseEntity<List<WorkCommentResponse>> getUrgentUnrespondedComments() {
        List<WorkCommentResponse> response = workCommentService.getUrgentUnrespondedComments();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/requiring-response")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO', 'ESPECIALISTA')")
    public ResponseEntity<List<WorkCommentResponse>> getCommentsRequiringResponse() {
        List<WorkCommentResponse> response = workCommentService.getCommentsRequiringResponse();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/client/{clientCui}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or #clientCui == authentication.principal.username")
    public ResponseEntity<Page<WorkCommentResponse>> getCommentsByClient(
            @PathVariable String clientCui,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<WorkCommentResponse> response = workCommentService.getCommentsByClient(clientCui, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{commentId}/respond")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO', 'ESPECIALISTA')")
    public ResponseEntity<WorkCommentResponse> respondToComment(
            @PathVariable Long commentId,
            @Valid @RequestBody WorkCommentResponseRequest request) {
        WorkCommentResponse response = workCommentService.respondToComment(commentId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/work/{workId}/type/{commentType}")
    public ResponseEntity<List<WorkCommentResponse>> getCommentsByWorkAndType(
            @PathVariable Long workId,
            @PathVariable String commentType) {
        List<WorkCommentResponse> response = workCommentService.getCommentsByWorkAndType(workId, commentType);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Long>> getCommentStatistics() {
        Map<String, Long> statistics = workCommentService.getCommentStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/work/{workId}/count")
    public ResponseEntity<Map<String, Long>> getCommentCountByWork(@PathVariable Long workId) {
        Long count = workCommentService.getCommentCountByWork(workId);
        return ResponseEntity.ok(Map.of("commentCount", count));
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        workCommentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}