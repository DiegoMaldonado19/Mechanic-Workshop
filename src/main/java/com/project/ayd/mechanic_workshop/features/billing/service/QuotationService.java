package com.project.ayd.mechanic_workshop.features.billing.service;

import com.project.ayd.mechanic_workshop.features.billing.dto.QuotationRequest;
import com.project.ayd.mechanic_workshop.features.billing.dto.QuotationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface QuotationService {

    QuotationResponse createQuotation(QuotationRequest request);

    QuotationResponse getQuotationById(Long id);

    QuotationResponse getQuotationByWorkId(Long workId);

    Page<QuotationResponse> getAllQuotations(Pageable pageable);

    Page<QuotationResponse> getQuotationsByClient(String clientCui, Pageable pageable);

    Page<QuotationResponse> getQuotationsByApprovalStatus(Boolean approved, Pageable pageable);

    Page<QuotationResponse> getQuotationsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Page<QuotationResponse> getQuotationsByCreatedBy(Long userId, Pageable pageable);

    QuotationResponse approveQuotation(Long id);

    QuotationResponse rejectQuotation(Long id);

    QuotationResponse updateQuotation(Long id, QuotationRequest request);

    List<QuotationResponse> getExpiredQuotations();

    void deleteQuotation(Long id);
}