package com.project.ayd.mechanic_workshop.features.billing.service;

import com.project.ayd.mechanic_workshop.features.billing.dto.InvoiceRequest;
import com.project.ayd.mechanic_workshop.features.billing.dto.InvoiceResponse;
import com.project.ayd.mechanic_workshop.features.billing.dto.BillingSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface InvoiceService {

    InvoiceResponse createInvoice(InvoiceRequest request);

    InvoiceResponse getInvoiceById(Long id);

    InvoiceResponse getInvoiceByWorkId(Long workId);

    Page<InvoiceResponse> getAllInvoices(Pageable pageable);

    Page<InvoiceResponse> getInvoicesByClient(String clientCui, Pageable pageable);

    Page<InvoiceResponse> getInvoicesByPaymentStatus(String statusName, Pageable pageable);

    Page<InvoiceResponse> getInvoicesByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<InvoiceResponse> getInvoicesByCreatedBy(Long userId, Pageable pageable);

    List<InvoiceResponse> getOverdueInvoices();

    InvoiceResponse updateInvoice(Long id, InvoiceRequest request);

    BillingSummaryResponse getBillingSummary(LocalDate startDate, LocalDate endDate);

    void deleteInvoice(Long id);
}
