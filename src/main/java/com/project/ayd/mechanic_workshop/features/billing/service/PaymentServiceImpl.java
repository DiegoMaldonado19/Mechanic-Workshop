package com.project.ayd.mechanic_workshop.features.billing.service;

import com.project.ayd.mechanic_workshop.features.auth.entity.User;
import com.project.ayd.mechanic_workshop.features.auth.repository.UserRepository;
import com.project.ayd.mechanic_workshop.features.billing.dto.PaymentRequest;
import com.project.ayd.mechanic_workshop.features.billing.dto.PaymentResponse;
import com.project.ayd.mechanic_workshop.features.billing.dto.PaymentMethodResponse;
import com.project.ayd.mechanic_workshop.features.billing.entity.Invoice;
import com.project.ayd.mechanic_workshop.features.billing.entity.Payment;
import com.project.ayd.mechanic_workshop.features.billing.entity.PaymentMethod;
import com.project.ayd.mechanic_workshop.features.billing.repository.InvoiceRepository;
import com.project.ayd.mechanic_workshop.features.billing.repository.PaymentRepository;
import com.project.ayd.mechanic_workshop.features.billing.repository.PaymentMethodRepository;
import com.project.ayd.mechanic_workshop.features.billing.events.PaymentCreatedEvent;
import com.project.ayd.mechanic_workshop.shared.exception.ResourceNotFoundException;
import com.project.ayd.mechanic_workshop.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

        private final PaymentRepository paymentRepository;
        private final InvoiceRepository invoiceRepository;
        private final PaymentMethodRepository paymentMethodRepository;
        private final UserRepository userRepository;
        private final ApplicationEventPublisher eventPublisher;

        @Override
        @Transactional
        public PaymentResponse createPayment(PaymentRequest request) {
                Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Factura no encontrada con ID: " + request.getInvoiceId()));

                PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId())
                                .orElseThrow(() -> new ResourceNotFoundException("Método de pago no encontrado con ID: "
                                                + request.getPaymentMethodId()));

                // Validar que el monto del pago no exceda el saldo pendiente
                BigDecimal totalPaid = paymentRepository.sumPaymentsByInvoiceId(invoice.getId());
                totalPaid = totalPaid != null ? totalPaid : BigDecimal.ZERO;
                BigDecimal pendingAmount = invoice.getTotalAmount().subtract(totalPaid);

                if (request.getAmount().compareTo(pendingAmount) > 0) {
                        throw new BusinessException("El monto del pago (" + request.getAmount() +
                                        ") excede el saldo pendiente (" + pendingAmount + ")");
                }

                User currentUser = getCurrentUser();

                Payment payment = Payment.builder()
                                .invoice(invoice)
                                .amount(request.getAmount())
                                .paymentMethod(paymentMethod)
                                .paymentDate(request.getPaymentDate() != null ? request.getPaymentDate()
                                                : LocalDateTime.now())
                                .referenceNumber(request.getReferenceNumber())
                                .notes(request.getNotes())
                                .receivedBy(currentUser)
                                .build();

                payment = paymentRepository.save(payment);

                // Publicar evento de pago creado
                publishPaymentCreatedEvent(payment, invoice, totalPaid.add(request.getAmount()));

                return mapToPaymentResponse(payment);
        }

        @Override
        @Transactional(readOnly = true)
        public PaymentResponse getPaymentById(Long id) {
                Payment payment = paymentRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con ID: " + id));
                return mapToPaymentResponse(payment);
        }

        @Override
        @Transactional(readOnly = true)
        public List<PaymentResponse> getPaymentsByInvoiceId(Long invoiceId) {
                return paymentRepository.findByInvoiceId(invoiceId)
                                .stream()
                                .map(this::mapToPaymentResponse)
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public Page<PaymentResponse> getAllPayments(Pageable pageable) {
                return paymentRepository.findAll(pageable)
                                .map(this::mapToPaymentResponse);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<PaymentResponse> getPaymentsByClient(String clientCui, Pageable pageable) {
                return paymentRepository.findByClientCui(clientCui, pageable)
                                .map(this::mapToPaymentResponse);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<PaymentResponse> getPaymentsByMethod(Long methodId, Pageable pageable) {
                return paymentRepository.findByPaymentMethodId(methodId, pageable)
                                .map(this::mapToPaymentResponse);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<PaymentResponse> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate,
                        Pageable pageable) {
                return paymentRepository.findByPaymentDateRange(startDate, endDate, pageable)
                                .map(this::mapToPaymentResponse);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<PaymentResponse> getPaymentsByReceivedBy(Long userId, Pageable pageable) {
                return paymentRepository.findByReceivedById(userId, pageable)
                                .map(this::mapToPaymentResponse);
        }

        @Override
        @Transactional(readOnly = true)
        public List<PaymentMethodResponse> getAllPaymentMethods() {
                return paymentMethodRepository.findAll()
                                .stream()
                                .map(this::mapToPaymentMethodResponse)
                                .toList();
        }

        @Override
        @Transactional
        public void deletePayment(Long id) {
                Payment payment = paymentRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con ID: " + id));

                // Validación adicional: verificar si el pago puede ser eliminado
                // Esto podría depender de las reglas de negocio específicas
                LocalDateTime cutoffDate = LocalDateTime.now().minusHours(24);
                if (payment.getCreatedAt().isBefore(cutoffDate)) {
                        throw new BusinessException("No se pueden eliminar pagos con más de 24 horas de antigüedad");
                }

                paymentRepository.delete(payment);
        }

        private PaymentResponse mapToPaymentResponse(Payment payment) {
                return PaymentResponse.builder()
                                .id(payment.getId())
                                .invoiceId(payment.getInvoice().getId())
                                .vehicleLicensePlate(payment.getInvoice().getWork().getVehicle().getLicensePlate())
                                .clientName(payment.getInvoice().getWork().getVehicle().getOwner().getFirstName() + " "
                                                +
                                                payment.getInvoice().getWork().getVehicle().getOwner().getLastName())
                                .amount(payment.getAmount())
                                .paymentMethodName(payment.getPaymentMethod().getName())
                                .paymentDate(payment.getPaymentDate())
                                .referenceNumber(payment.getReferenceNumber())
                                .notes(payment.getNotes())
                                .receivedByUsername(payment.getReceivedBy().getUsername())
                                .createdAt(payment.getCreatedAt())
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

        private User getCurrentUser() {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                return userRepository.findByUsername(username)
                                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
        }

        private void publishPaymentCreatedEvent(Payment payment, Invoice invoice, BigDecimal totalAmountPaid) {
                BigDecimal pendingAmount = invoice.getTotalAmount().subtract(totalAmountPaid);
                boolean isFullyPaid = pendingAmount.compareTo(BigDecimal.ZERO) <= 0;

                PaymentCreatedEvent event = PaymentCreatedEvent.builder()
                                .paymentId(payment.getId())
                                .invoiceId(invoice.getId())
                                .paymentAmount(payment.getAmount())
                                .paymentMethodName(payment.getPaymentMethod().getName())
                                .clientCui(invoice.getWork().getVehicle().getOwner().getCui())
                                .clientName(invoice.getWork().getVehicle().getOwner().getFirstName() + " " +
                                                invoice.getWork().getVehicle().getOwner().getLastName())
                                .vehicleLicensePlate(invoice.getWork().getVehicle().getLicensePlate())
                                .paymentDate(payment.getPaymentDate())
                                .receivedByUsername(payment.getReceivedBy().getUsername())
                                .invoiceTotalAmount(invoice.getTotalAmount())
                                .totalAmountPaid(totalAmountPaid)
                                .pendingAmount(pendingAmount)
                                .isFullyPaid(isFullyPaid)
                                .build();

                eventPublisher.publishEvent(event);
        }
}