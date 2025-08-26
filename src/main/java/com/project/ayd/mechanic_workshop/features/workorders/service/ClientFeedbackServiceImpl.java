package com.project.ayd.mechanic_workshop.features.workorders.service;

import com.project.ayd.mechanic_workshop.features.auth.entity.Person;
import com.project.ayd.mechanic_workshop.features.auth.repository.PersonRepository;
import com.project.ayd.mechanic_workshop.features.workorders.dto.ClientFeedbackRequest;
import com.project.ayd.mechanic_workshop.features.workorders.dto.ClientFeedbackResponse;
import com.project.ayd.mechanic_workshop.features.workorders.entity.ClientFeedback;
import com.project.ayd.mechanic_workshop.features.workorders.entity.Work;
import com.project.ayd.mechanic_workshop.features.workorders.enums.WorkOrderStatus;
import com.project.ayd.mechanic_workshop.features.workorders.repository.ClientFeedbackRepository;
import com.project.ayd.mechanic_workshop.features.workorders.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientFeedbackServiceImpl implements ClientFeedbackService {

    private final ClientFeedbackRepository clientFeedbackRepository;
    private final WorkRepository workRepository;
    private final PersonRepository personRepository;

    @Override
    @Transactional
    public ClientFeedbackResponse createFeedback(ClientFeedbackRequest request) {
        log.info("Creating feedback for work ID: {}", request.getWorkId());

        Work work = workRepository.findByIdWithDetails(request.getWorkId())
                .orElseThrow(
                        () -> new IllegalArgumentException("Work order not found with ID: " + request.getWorkId()));

        // Verificar que el trabajo esté completado
        if (!work.getWorkStatus().getId().equals(WorkOrderStatus.COMPLETED.getId())) {
            throw new IllegalStateException("Feedback can only be provided for completed work orders");
        }

        String currentUserCui = SecurityContextHolder.getContext().getAuthentication().getName();
        Person client = personRepository.findByCui(currentUserCui)
                .orElseThrow(() -> new IllegalArgumentException("Client not found with CUI: " + currentUserCui));

        // Verificar que el cliente es el propietario del vehículo
        if (!work.getVehicle().getOwner().getCui().equals(client.getCui())) {
            throw new IllegalStateException("You can only provide feedback for your own vehicle's work orders");
        }

        // Verificar que no existe feedback previo
        if (clientFeedbackRepository.existsByWorkIdAndClientCui(request.getWorkId(), currentUserCui)) {
            throw new IllegalStateException("Feedback already exists for this work order");
        }

        ClientFeedback feedback = ClientFeedback.builder()
                .work(work)
                .client(client)
                .rating(request.getRating())
                .comments(request.getComments())
                .wouldRecommend(request.getWouldRecommend())
                .build();

        feedback = clientFeedbackRepository.save(feedback);
        log.info("Feedback created successfully with ID: {}", feedback.getId());

        return mapToFeedbackResponse(feedback);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientFeedbackResponse getFeedbackById(Long feedbackId) {
        ClientFeedback feedback = clientFeedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new IllegalArgumentException("Feedback not found with ID: " + feedbackId));
        return mapToFeedbackResponse(feedback);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientFeedbackResponse getFeedbackByWorkId(Long workId) {
        ClientFeedback feedback = clientFeedbackRepository.findByWorkIdWithDetails(workId)
                .orElseThrow(() -> new IllegalArgumentException("Feedback not found for work ID: " + workId));
        return mapToFeedbackResponse(feedback);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientFeedbackResponse> getFeedbackByClient(String clientCui) {
        return clientFeedbackRepository.findByClientCuiOrderByCreatedAtDesc(clientCui)
                .stream()
                .map(this::mapToFeedbackResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientFeedbackResponse> getAllFeedback(Pageable pageable) {
        return clientFeedbackRepository.findAllOrderByCreatedAtDesc(pageable)
                .map(this::mapToFeedbackResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientFeedbackResponse> getFeedbackByRating(Integer rating) {
        return clientFeedbackRepository.findByRatingOrderByCreatedAtDesc(rating)
                .stream()
                .map(this::mapToFeedbackResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientFeedbackResponse> getFeedbackByRecommendation(Boolean wouldRecommend) {
        return clientFeedbackRepository.findByWouldRecommendOrderByCreatedAtDesc(wouldRecommend)
                .stream()
                .map(this::mapToFeedbackResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientFeedbackResponse> getFeedbackByEmployee(Long employeeId, Pageable pageable) {
        return clientFeedbackRepository.findByAssignedEmployeeId(employeeId, pageable)
                .map(this::mapToFeedbackResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageRating() {
        return clientFeedbackRepository.getAverageRating();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Integer, Long> getRatingDistribution() {
        Map<Integer, Long> distribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            Long count = clientFeedbackRepository.countByRating(i);
            distribution.put(i, count);
        }
        return distribution;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getRecommendationCount() {
        return clientFeedbackRepository.countWouldRecommend();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canClientLeaveFeedback(Long workId, String clientCui) {
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new IllegalArgumentException("Work order not found"));

        if (!work.getWorkStatus().getId().equals(WorkOrderStatus.COMPLETED.getId())) {
            return false;
        }

        if (!work.getVehicle().getOwner().getCui().equals(clientCui)) {
            return false;
        }

        return !clientFeedbackRepository.existsByWorkIdAndClientCui(workId, clientCui);
    }

    @Override
    @Transactional
    public void deleteFeedback(Long feedbackId) {
        log.info("Deleting feedback with ID: {}", feedbackId);

        ClientFeedback feedback = clientFeedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new IllegalArgumentException("Feedback not found with ID: " + feedbackId));

        clientFeedbackRepository.delete(feedback);
        log.info("Feedback deleted successfully");
    }

    private ClientFeedbackResponse mapToFeedbackResponse(ClientFeedback feedback) {
        String clientName = feedback.getClient().getFirstName() + " " + feedback.getClient().getLastName();
        String vehicleLicensePlate = feedback.getWork().getVehicle().getLicensePlate();
        String serviceTypeName = feedback.getWork().getServiceType().getName();

        return ClientFeedbackResponse.builder()
                .id(feedback.getId())
                .workId(feedback.getWork().getId())
                .clientCui(feedback.getClient().getCui())
                .clientName(clientName)
                .rating(feedback.getRating())
                .comments(feedback.getComments())
                .wouldRecommend(feedback.getWouldRecommend())
                .createdAt(feedback.getCreatedAt())
                .vehicleLicensePlate(vehicleLicensePlate)
                .serviceTypeName(serviceTypeName)
                .build();
    }
}