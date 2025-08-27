package com.project.ayd.mechanic_workshop.features.billing.service;

import com.project.ayd.mechanic_workshop.features.auth.entity.User;
import com.project.ayd.mechanic_workshop.features.auth.repository.UserRepository;
import com.project.ayd.mechanic_workshop.features.billing.dto.InvoiceRequest;
import com.project.ayd.mechanic_workshop.features.billing.dto.InvoiceResponse;
import com.project.ayd.mechanic_workshop.features.billing.dto.BillingSummaryResponse;
import com.project.ayd.mechanic_workshop.features.billing.entity.Invoice;
import com.project.ayd.mechanic_workshop.features.billing.entity.PaymentStatus;
import com.project.ayd.mechanic_workshop.features.billing.entity.Quotation;
import com.project.ayd.mechanic_workshop.features.billing.repository.InvoiceRepository;
import com.project.ayd.mechanic_workshop.features.billing.repository.PaymentRepository;
import com.project.ayd.mechanic_workshop.features.billing.repository.PaymentStatusRepository;
import com.project.ayd.mechanic_workshop.features.billing.repository.QuotationRepository;
import com.project.ayd.mechanic_workshop.features.workorders.entity.Work;
import com.project.ayd.mechanic_workshop.features.workorders.repository.WorkRepository;
import com.project.ayd.mechanic_workshop.features.billing.events.InvoiceCreatedEvent;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

        private final InvoiceRepository invoiceRepository;
        private final PaymentRepository paymentRepository;
        private final QuotationRepository quotationRepository;
        private final WorkRepository workRepository;
        private final UserRepository userRepository;
        private final PaymentStatusRepository paymentStatusRepository;
        private final ApplicationEventPublisher eventPublisher;

        @Override
        @Transactional
        public InvoiceResponse createInvoice(InvoiceRequest request) {
                Work work = workRepository.findById(request.getWorkId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Trabajo no encontrado con ID: " + request.getWorkId()));

                if (invoiceRepository.findByWorkId(request.getWorkId()).isPresent()) {
                        throw new BusinessException("Ya existe una factura para este trabajo");
                }

                Quotation quotation = null;
                if (request.getQuotationId() != null) {
                        quotation = quotationRepository.findById(request.getQuotationId())
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Cotización no encontrada con ID: "
                                                                        + request.getQuotationId()));

                        if (!quotation.getClientApproved()) {
                                throw new BusinessException(
                                                "La cotización debe estar aprobada para generar una factura");
                        }
                }

                PaymentStatus pendingStatus = paymentStatusRepository.findByNameIgnoreCase("Pendiente")
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Estado de pago 'Pendiente' no encontrado"));

                User currentUser = getCurrentUser();

                BigDecimal taxAmount = request.getTaxAmount() != null ? request.getTaxAmount() : BigDecimal.ZERO;
                BigDecimal totalAmount = request.getSubtotal().add(taxAmount);

                Invoice invoice = Invoice.builder()
                                .work(work)
                                .quotation(quotation)
                                .subtotal(request.getSubtotal())
                                .taxAmount(taxAmount)
                                .totalAmount(totalAmount)
                                .dueDate(request.getDueDate() != null ? request.getDueDate()
                                                : LocalDate.now().plusDays(30))
                                .paymentStatus(pendingStatus)
                                .createdBy(currentUser)
                                .build();

                invoice = invoiceRepository.save(invoice);

                // Publicar evento de factura creada
                publishInvoiceCreatedEvent(invoice);

                return mapToInvoiceResponse(invoice);
        }

        @Override
        @Transactional(readOnly = true)
        public InvoiceResponse getInvoiceById(Long id) {
                Invoice invoice = invoiceRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Factura no encontrada con ID: " + id));
                return mapToInvoiceResponse(invoice);
        }

        @Override
        @Transactional(readOnly = true)
        public InvoiceResponse getInvoiceByWorkId(Long workId) {
                Invoice invoice = invoiceRepository.findByWorkId(workId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Factura no encontrada para el trabajo con ID: " + workId));
                return mapToInvoiceResponse(invoice);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<InvoiceResponse> getAllInvoices(Pageable pageable) {
                return invoiceRepository.findAll(pageable)
                                .map(this::mapToInvoiceResponse);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<InvoiceResponse> getInvoicesByClient(String clientCui, Pageable pageable) {
                return invoiceRepository.findByClientCui(clientCui, pageable)
                                .map(this::mapToInvoiceResponse);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<InvoiceResponse> getInvoicesByPaymentStatus(String statusName, Pageable pageable) {
                return invoiceRepository.findByPaymentStatusName(statusName, pageable)
                                .map(this::mapToInvoiceResponse);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<InvoiceResponse> getInvoicesByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
                return invoiceRepository.findByIssuedDateRange(startDate, endDate, pageable)
                                .map(this::mapToInvoiceResponse);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<InvoiceResponse> getInvoicesByCreatedBy(Long userId, Pageable pageable) {
                return invoiceRepository.findByCreatedById(userId, pageable)
                                .map(this::mapToInvoiceResponse);
        }

        @Override
        @Transactional(readOnly = true)
        public List<InvoiceResponse> getOverdueInvoices() {
                return invoiceRepository.findOverdueInvoices(LocalDate.now())
                                .stream()
                                .map(this::mapToInvoiceResponse)
                                .toList();
        }

        @Override
        @Transactional
        public InvoiceResponse updateInvoice(Long id, InvoiceRequest request) {
                Invoice invoice = invoiceRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Factura no encontrada con ID: " + id));

                if ("Pagado".equalsIgnoreCase(invoice.getPaymentStatus().getName())) {
                        throw new BusinessException("No se puede modificar una factura pagada");
                }

                BigDecimal taxAmount = request.getTaxAmount() != null ? request.getTaxAmount() : BigDecimal.ZERO;
                BigDecimal totalAmount = request.getSubtotal().add(taxAmount);

                invoice.setSubtotal(request.getSubtotal());
                invoice.setTaxAmount(taxAmount);
                invoice.setTotalAmount(totalAmount);
                if (request.getDueDate() != null) {
                        invoice.setDueDate(request.getDueDate());
                }
                invoice.setUpdatedAt(LocalDateTime.now());

                invoice = invoiceRepository.save(invoice);
                return mapToInvoiceResponse(invoice);
        }

        @Override
        @Transactional(readOnly = true)
        public BillingSummaryResponse getBillingSummary(LocalDate startDate, LocalDate endDate) {
                LocalDateTime startDateTime = startDate.atStartOfDay();
                LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

                Long totalQuotations = quotationRepository.count();
                Long approvedQuotations = quotationRepository.countApprovedQuotationsByDateRange(startDateTime,
                                endDateTime);
                BigDecimal approvedQuotationsAmount = quotationRepository
                                .sumApprovedQuotationsByDateRange(startDateTime, endDateTime);

                Long totalInvoices = invoiceRepository.countInvoicesByDateRange(startDate, endDate);
                BigDecimal paidAmount = invoiceRepository.sumPaidInvoicesByDateRange(startDate, endDate);
                BigDecimal overdueAmount = invoiceRepository.sumOverdueInvoicesAmount(LocalDate.now());

                Long totalPayments = paymentRepository.countPaymentsByDateRange(startDateTime, endDateTime);
                BigDecimal totalPaymentsAmount = paymentRepository.sumPaymentsByDateRange(startDateTime, endDateTime);

                Double quotationApprovalRate = totalQuotations > 0
                                ? (approvedQuotations.doubleValue() / totalQuotations.doubleValue()) * 100
                                : 0.0;

                return BillingSummaryResponse.builder()
                                .totalQuotations(totalQuotations)
                                .approvedQuotations(approvedQuotations)
                                .approvedQuotationsAmount(approvedQuotationsAmount != null ? approvedQuotationsAmount
                                                : BigDecimal.ZERO)
                                .totalInvoices(totalInvoices)
                                .paidAmount(paidAmount != null ? paidAmount : BigDecimal.ZERO)
                                .overdueAmount(overdueAmount != null ? overdueAmount : BigDecimal.ZERO)
                                .totalPayments(totalPayments)
                                .totalPaymentsAmount(
                                                totalPaymentsAmount != null ? totalPaymentsAmount : BigDecimal.ZERO)
                                .quotationApprovalRate(quotationApprovalRate)
                                .build();
        }

        @Override
        @Transactional
        public void deleteInvoice(Long id) {
                Invoice invoice = invoiceRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Factura no encontrada con ID: " + id));

                if ("Pagado".equalsIgnoreCase(invoice.getPaymentStatus().getName())
                                || "Parcial".equalsIgnoreCase(invoice.getPaymentStatus().getName())) {
                        throw new BusinessException("No se puede eliminar una factura con pagos registrados");
                }

                invoiceRepository.delete(invoice);
        }

        private InvoiceResponse mapToInvoiceResponse(Invoice invoice) {
                BigDecimal amountPaid = paymentRepository.sumPaymentsByInvoiceId(invoice.getId());
                amountPaid = amountPaid != null ? amountPaid : BigDecimal.ZERO;
                BigDecimal pendingAmount = invoice.getTotalAmount().subtract(amountPaid);

                return InvoiceResponse.builder()
                                .id(invoice.getId())
                                .workId(invoice.getWork().getId())
                                .workDescription(invoice.getWork().getProblemDescription())
                                .quotationId(invoice.getQuotation() != null ? invoice.getQuotation().getId() : null)
                                .vehicleLicensePlate(invoice.getWork().getVehicle().getLicensePlate())
                                .vehicleModel(invoice.getWork().getVehicle().getModel().getName())
                                .clientName(invoice.getWork().getVehicle().getOwner().getFirstName() + " "
                                                + invoice.getWork().getVehicle().getOwner().getLastName())
                                .subtotal(invoice.getSubtotal())
                                .taxAmount(invoice.getTaxAmount())
                                .totalAmount(invoice.getTotalAmount())
                                .issuedDate(invoice.getIssuedDate())
                                .dueDate(invoice.getDueDate())
                                .paymentStatus(invoice.getPaymentStatus().getName())
                                .amountPaid(amountPaid)
                                .pendingAmount(pendingAmount)
                                .createdByUsername(invoice.getCreatedBy().getUsername())
                                .createdAt(invoice.getCreatedAt())
                                .updatedAt(invoice.getUpdatedAt())
                                .build();
        }

        private User getCurrentUser() {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                return userRepository.findByUsername(username)
                                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
        }

        private void publishInvoiceCreatedEvent(Invoice invoice) {
                InvoiceCreatedEvent event = InvoiceCreatedEvent.builder()
                                .invoiceId(invoice.getId())
                                .workId(invoice.getWork().getId())
                                .workDescription(invoice.getWork().getProblemDescription())
                                .quotationId(invoice.getQuotation() != null ? invoice.getQuotation().getId() : null)
                                .clientCui(invoice.getWork().getVehicle().getOwnerCui())
                                .clientName(invoice.getWork().getVehicle().getOwner().getFirstName() + " " +
                                                invoice.getWork().getVehicle().getOwner().getLastName())
                                .vehicleLicensePlate(invoice.getWork().getVehicle().getLicensePlate())
                                .vehicleModel(invoice.getWork().getVehicle().getModel().getName())
                                .subtotal(invoice.getSubtotal())
                                .taxAmount(invoice.getTaxAmount())
                                .totalAmount(invoice.getTotalAmount())
                                .issuedDate(invoice.getIssuedDate())
                                .dueDate(invoice.getDueDate())
                                .createdByUsername(invoice.getCreatedBy().getUsername())
                                .createdAt(invoice.getCreatedAt())
                                .build();

                eventPublisher.publishEvent(event);
        }
}