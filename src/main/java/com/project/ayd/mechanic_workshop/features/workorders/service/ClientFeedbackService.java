package com.project.ayd.mechanic_workshop.features.workorders.service;

import com.project.ayd.mechanic_workshop.features.workorders.dto.ClientFeedbackRequest;
import com.project.ayd.mechanic_workshop.features.workorders.dto.ClientFeedbackResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ClientFeedbackService {

    ClientFeedbackResponse createFeedback(ClientFeedbackRequest request);

    ClientFeedbackResponse getFeedbackById(Long feedbackId);

    ClientFeedbackResponse getFeedbackByWorkId(Long workId);

    List<ClientFeedbackResponse> getFeedbackByClient(String clientCui);

    Page<ClientFeedbackResponse> getAllFeedback(Pageable pageable);

    List<ClientFeedbackResponse> getFeedbackByRating(Integer rating);

    List<ClientFeedbackResponse> getFeedbackByRecommendation(Boolean wouldRecommend);

    Page<ClientFeedbackResponse> getFeedbackByEmployee(Long employeeId, Pageable pageable);

    Double getAverageRating();

    Map<Integer, Long> getRatingDistribution();

    Long getRecommendationCount();

    boolean canClientLeaveFeedback(Long workId, String clientCui);

    void deleteFeedback(Long feedbackId);
}