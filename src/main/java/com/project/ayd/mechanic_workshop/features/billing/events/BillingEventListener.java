package com.project.ayd.mechanic_workshop.features.billing.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BillingEventListener {

    @EventListener
    @Async
    public void handlePaymentCreated(PaymentCreatedEvent event) {
        log.info("Pago creado - ID: {}, Monto: {}, Cliente: {}, Factura: {}",
                event.getPaymentId(),
                event.getPaymentAmount(),
                event.getClientName(),
                event.getInvoiceId());

        // Lógica para manejar el evento de pago creado
        if (event.isFullyPaid()) {
            log.info("Factura {} ha sido pagada completamente", event.getInvoiceId());
            // Aquí se podría enviar una notificación al cliente
            // o actualizar el estado del trabajo asociado
        } else {
            log.info("Pago parcial recibido para factura {}. Pendiente: {}",
                    event.getInvoiceId(),
                    event.getPendingAmount());
        }
    }

    @EventListener
    @Async
    public void handleInvoiceCreated(InvoiceCreatedEvent event) {
        log.info("Factura creada - ID: {}, Total: {}, Cliente: {}, Vencimiento: {}",
                event.getInvoiceId(),
                event.getTotalAmount(),
                event.getClientName(),
                event.getDueDate());

        // Lógica para manejar el evento de factura creada
        // Por ejemplo, programar recordatorios de vencimiento
        // o enviar la factura por email al cliente
    }

    @EventListener
    @Async
    public void handleQuotationApproved(QuotationApprovedEvent event) {
        log.info("Cotización aprobada - ID: {}, Total: {}, Cliente: {}",
                event.getQuotationId(),
                event.getTotalAmount(),
                event.getClientName());

        // Lógica para manejar el evento de cotización aprobada
        // Por ejemplo, notificar al empleado asignado para comenzar el trabajo
        // o actualizar el estado del trabajo
    }
}