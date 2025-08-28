package com.project.ayd.mechanic_workshop.features.billing.service;

import com.project.ayd.mechanic_workshop.features.billing.dto.PaymentStatusResponse;
import com.project.ayd.mechanic_workshop.features.billing.dto.PaymentMethodResponse;
import com.project.ayd.mechanic_workshop.features.billing.entity.PaymentStatus;
import com.project.ayd.mechanic_workshop.features.billing.entity.PaymentMethod;
import com.project.ayd.mechanic_workshop.features.billing.repository.PaymentStatusRepository;
import com.project.ayd.mechanic_workshop.features.billing.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BillingStatusService {

    private final PaymentStatusRepository paymentStatusRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    @Transactional(readOnly = true)
    public List<PaymentStatusResponse> getAllPaymentStatuses() {
        return paymentStatusRepository.findAll()
                .stream()
                .map(this::mapToPaymentStatusResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentMethodResponse> getAllPaymentMethods() {
        return paymentMethodRepository.findAll()
                .stream()
                .map(this::mapToPaymentMethodResponse)
                .toList();
    }

    private PaymentStatusResponse mapToPaymentStatusResponse(PaymentStatus status) {
        return PaymentStatusResponse.builder()
                .id(status.getId())
                .name(status.getName())
                .description(status.getDescription())
                .createdAt(status.getCreatedAt())
                .build();
    }

    private PaymentMethodResponse mapToPaymentMethodResponse(PaymentMethod method) {
        return PaymentMethodResponse.builder()
                .id(method.getId())
                .name(method.getName())
                .description(method.getDescription())
                .createdAt(method.getCreatedAt())
                .build();
    }
}