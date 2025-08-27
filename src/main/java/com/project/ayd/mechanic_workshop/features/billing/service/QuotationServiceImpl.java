package com.project.ayd.mechanic_workshop.features.billing.service;

import com.project.ayd.mechanic_workshop.features.auth.entity.User;
import com.project.ayd.mechanic_workshop.features.auth.repository.UserRepository;
import com.project.ayd.mechanic_workshop.features.billing.dto.QuotationRequest;
import com.project.ayd.mechanic_workshop.features.billing.dto.QuotationResponse;
import com.project.ayd.mechanic_workshop.features.billing.entity.Quotation;
import com.project.ayd.mechanic_workshop.features.billing.repository.QuotationRepository;
import com.project.ayd.mechanic_workshop.features.workorders.entity.Work;
import com.project.ayd.mechanic_workshop.features.workorders.repository.WorkRepository;
import com.project.ayd.mechanic_workshop.features.billing.events.QuotationApprovedEvent;
import com.project.ayd.mechanic_workshop.shared.exception.ResourceNotFoundException;
import com.project.ayd.mechanic_workshop.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuotationServiceImpl implements QuotationService {

    private final QuotationRepository quotationRepository;
    private final WorkRepository workRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public QuotationResponse createQuotation(QuotationRequest request) {
        Work work = workRepository.findById(request.getWorkId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Trabajo no encontrado con ID: " + request.getWorkId()));

        if (quotationRepository.findByWorkId(request.getWorkId()).isPresent()) {
            throw new BusinessException("Ya existe una cotización para este trabajo");
        }

        User currentUser = getCurrentUser();

        Quotation quotation = Quotation.builder()
                .work(work)
                .totalPartsCost(request.getTotalPartsCost())
                .totalLaborCost(request.getTotalLaborCost())
                .totalAmount(request.getTotalPartsCost().add(request.getTotalLaborCost()))
                .validUntil(request.getValidUntil() != null ? request.getValidUntil() : LocalDate.now().plusDays(30))
                .createdBy(currentUser)
                .build();

        quotation = quotationRepository.save(quotation);

        // Publicar evento de cotización aprobada
        publishQuotationApprovedEvent(quotation);

        return mapToQuotationResponse(quotation);
    }

    @Override
    @Transactional(readOnly = true)
    public QuotationResponse getQuotationById(Long id) {
        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada con ID: " + id));
        return mapToQuotationResponse(quotation);
    }

    @Override
    @Transactional(readOnly = true)
    public QuotationResponse getQuotationByWorkId(Long workId) {
        Quotation quotation = quotationRepository.findByWorkId(workId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cotización no encontrada para el trabajo con ID: " + workId));
        return mapToQuotationResponse(quotation);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuotationResponse> getAllQuotations(Pageable pageable) {
        return quotationRepository.findAll(pageable)
                .map(this::mapToQuotationResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuotationResponse> getQuotationsByClient(String clientCui, Pageable pageable) {
        return quotationRepository.findByClientCui(clientCui, pageable)
                .map(this::mapToQuotationResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuotationResponse> getQuotationsByApprovalStatus(Boolean approved, Pageable pageable) {
        return quotationRepository.findByClientApproved(approved, pageable)
                .map(this::mapToQuotationResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuotationResponse> getQuotationsByDateRange(LocalDateTime startDate, LocalDateTime endDate,
            Pageable pageable) {
        return quotationRepository.findByDateRange(startDate, endDate, pageable)
                .map(this::mapToQuotationResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuotationResponse> getQuotationsByCreatedBy(Long userId, Pageable pageable) {
        return quotationRepository.findByCreatedById(userId, pageable)
                .map(this::mapToQuotationResponse);
    }

    @Override
    @Transactional
    public QuotationResponse approveQuotation(Long id) {
        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada con ID: " + id));

        if (quotation.getClientApproved()) {
            throw new BusinessException("La cotización ya está aprobada");
        }

        if (quotation.getValidUntil().isBefore(LocalDate.now())) {
            throw new BusinessException("La cotización ha expirado y no puede ser aprobada");
        }

        quotation.setClientApproved(true);
        quotation.setApprovedAt(LocalDateTime.now());
        quotation.setUpdatedAt(LocalDateTime.now());

        quotation = quotationRepository.save(quotation);
        return mapToQuotationResponse(quotation);
    }

    @Override
    @Transactional
    public QuotationResponse rejectQuotation(Long id) {
        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada con ID: " + id));

        if (quotation.getClientApproved()) {
            throw new BusinessException("No se puede rechazar una cotización ya aprobada");
        }

        quotation.setClientApproved(false);
        quotation.setUpdatedAt(LocalDateTime.now());

        quotation = quotationRepository.save(quotation);
        return mapToQuotationResponse(quotation);
    }

    @Override
    @Transactional
    public QuotationResponse updateQuotation(Long id, QuotationRequest request) {
        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada con ID: " + id));

        if (quotation.getClientApproved()) {
            throw new BusinessException("No se puede modificar una cotización aprobada");
        }

        quotation.setTotalPartsCost(request.getTotalPartsCost());
        quotation.setTotalLaborCost(request.getTotalLaborCost());
        quotation.setTotalAmount(request.getTotalPartsCost().add(request.getTotalLaborCost()));
        if (request.getValidUntil() != null) {
            quotation.setValidUntil(request.getValidUntil());
        }
        quotation.setUpdatedAt(LocalDateTime.now());

        quotation = quotationRepository.save(quotation);
        return mapToQuotationResponse(quotation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuotationResponse> getExpiredQuotations() {
        return quotationRepository.findExpiredQuotations(LocalDate.now())
                .stream()
                .map(this::mapToQuotationResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteQuotation(Long id) {
        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada con ID: " + id));

        if (quotation.getClientApproved()) {
            throw new BusinessException("No se puede eliminar una cotización aprobada");
        }

        quotationRepository.delete(quotation);
    }

    private QuotationResponse mapToQuotationResponse(Quotation quotation) {
        return QuotationResponse.builder()
                .id(quotation.getId())
                .workId(quotation.getWork().getId())
                .workDescription(quotation.getWork().getProblemDescription())
                .vehicleLicensePlate(quotation.getWork().getVehicle().getLicensePlate())
                .vehicleModel(quotation.getWork().getVehicle().getModel().getName())
                .clientName(quotation.getWork().getVehicle().getOwner().getFirstName() + " "
                        + quotation.getWork().getVehicle().getOwner().getLastName())
                .totalPartsCost(quotation.getTotalPartsCost())
                .totalLaborCost(quotation.getTotalLaborCost())
                .totalAmount(quotation.getTotalAmount())
                .validUntil(quotation.getValidUntil())
                .clientApproved(quotation.getClientApproved())
                .approvedAt(quotation.getApprovedAt())
                .createdByUsername(quotation.getCreatedBy().getUsername())
                .createdAt(quotation.getCreatedAt())
                .updatedAt(quotation.getUpdatedAt())
                .build();
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
    }

    private void publishQuotationApprovedEvent(Quotation quotation) {
        QuotationApprovedEvent event = QuotationApprovedEvent.builder()
                .quotationId(quotation.getId())
                .workId(quotation.getWork().getId())
                .workDescription(quotation.getWork().getProblemDescription())
                .clientCui(quotation.getWork().getVehicle().getOwnerCui())
                .clientName(quotation.getWork().getVehicle().getOwner().getFirstName() + " " +
                        quotation.getWork().getVehicle().getOwner().getLastName())
                .vehicleLicensePlate(quotation.getWork().getVehicle().getLicensePlate())
                .vehicleModel(quotation.getWork().getVehicle().getModel().getName())
                .totalPartsCost(quotation.getTotalPartsCost())
                .totalLaborCost(quotation.getTotalLaborCost())
                .totalAmount(quotation.getTotalAmount())
                .validUntil(quotation.getValidUntil())
                .approvedAt(quotation.getApprovedAt())
                .createdByUsername(quotation.getCreatedBy().getUsername())
                .build();

        eventPublisher.publishEvent(event);
    }
}