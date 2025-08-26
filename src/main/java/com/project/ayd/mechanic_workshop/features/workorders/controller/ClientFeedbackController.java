package com.project.ayd.mechanic_workshop.features.workorders.controller;

import com.project.ayd.mechanic_workshop.features.workorders.dto.ClientFeedbackRequest;
import com.project.ayd.mechanic_workshop.features.workorders.dto.ClientFeedbackResponse;
import com.project.ayd.mechanic_workshop.features.workorders.service.ClientFeedbackService;
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
@RequestMapping("/workorders/feedback")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CLIENTE')")
public class ClientFeedbackController {

    private final ClientFeedbackService clientFeedbackService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ClientFeedbackResponse> createFeedback(@Valid @RequestBody ClientFeedbackRequest request) {
        ClientFeedbackResponse response = clientFeedbackService.createFeedback(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{feedbackId}")
    public ResponseEntity<ClientFeedbackResponse> getFeedbackById(@PathVariable Long feedbackId) {
        ClientFeedbackResponse response = clientFeedbackService.getFeedbackById(feedbackId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/work/{workId}")
    public ResponseEntity<ClientFeedbackResponse> getFeedbackByWorkId(@PathVariable Long workId) {
        ClientFeedbackResponse response = clientFeedbackService.getFeedbackByWorkId(workId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/client/{clientCui}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or #clientCui == authentication.principal.username")
    public ResponseEntity<List<ClientFeedbackResponse>> getFeedbackByClient(@PathVariable String clientCui) {
        List<ClientFeedbackResponse> response = clientFeedbackService.getFeedbackByClient(clientCui);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-feedback")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<ClientFeedbackResponse>> getMyFeedback() {
        String currentUserCui = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ClientFeedbackResponse> response = clientFeedbackService.getFeedbackByClient(currentUserCui);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Page<ClientFeedbackResponse>> getAllFeedback(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ClientFeedbackResponse> response = clientFeedbackService.getAllFeedback(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rating/{rating}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<ClientFeedbackResponse>> getFeedbackByRating(@PathVariable Integer rating) {
        List<ClientFeedbackResponse> response = clientFeedbackService.getFeedbackByRating(rating);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recommendation/{wouldRecommend}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<ClientFeedbackResponse>> getFeedbackByRecommendation(
            @PathVariable Boolean wouldRecommend) {
        List<ClientFeedbackResponse> response = clientFeedbackService.getFeedbackByRecommendation(wouldRecommend);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Page<ClientFeedbackResponse>> getFeedbackByEmployee(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ClientFeedbackResponse> response = clientFeedbackService.getFeedbackByEmployee(employeeId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics/average-rating")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Double>> getAverageRating() {
        Double averageRating = clientFeedbackService.getAverageRating();
        return ResponseEntity.ok(Map.of("averageRating", averageRating != null ? averageRating : 0.0));
    }

    @GetMapping("/statistics/rating-distribution")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<Integer, Long>> getRatingDistribution() {
        Map<Integer, Long> distribution = clientFeedbackService.getRatingDistribution();
        return ResponseEntity.ok(distribution);
    }

    @GetMapping("/statistics/recommendation-count")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Long>> getRecommendationCount() {
        Long count = clientFeedbackService.getRecommendationCount();
        return ResponseEntity.ok(Map.of("recommendationCount", count));
    }

    @GetMapping("/can-provide/{workId}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Map<String, Boolean>> canProvideFeedback(@PathVariable Long workId) {
        String currentUserCui = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean canProvide = clientFeedbackService.canClientLeaveFeedback(workId, currentUserCui);
        return ResponseEntity.ok(Map.of("canProvideFeedback", canProvide));
    }

    @DeleteMapping("/{feedbackId}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long feedbackId) {
        clientFeedbackService.deleteFeedback(feedbackId);
        return ResponseEntity.noContent().build();
    }
}