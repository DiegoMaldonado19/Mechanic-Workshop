package com.project.ayd.mechanic_workshop.features.billing.service;

import com.project.ayd.mechanic_workshop.features.billing.dto.PaymentRequest;
import com.project.ayd.mechanic_workshop.features.billing.dto.PaymentResponse;
import com.project.ayd.mechanic_workshop.features.billing.dto.PaymentMethodResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentService {

    PaymentResponse createPayment(PaymentRequest request);

    PaymentResponse getPaymentById(Long id);

    List<PaymentResponse> getPaymentsByInvoiceId(Long invoiceId);

    Page<PaymentResponse> getAllPayments(Pageable pageable);

    Page<PaymentResponse> getPaymentsByClient(String clientCui, Pageable pageable);

    Page<PaymentResponse> getPaymentsByMethod(Long methodId, Pageable pageable);

    Page<PaymentResponse> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Page<PaymentResponse> getPaymentsByReceivedBy(Long userId, Pageable pageable);

    List<PaymentMethodResponse> getAllPaymentMethods();

    void deletePayment(Long id);
}