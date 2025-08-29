package com.project.ayd.mechanic_workshop.features.billing.repository;

import com.project.ayd.mechanic_workshop.features.billing.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

        List<Payment> findByInvoiceId(Long invoiceId);

        @Query("SELECT p FROM Payment p WHERE p.invoice.work.vehicle.owner.cui = :clientCui")
        Page<Payment> findByClientCui(@Param("clientCui") String clientCui, Pageable pageable);

        @Query("SELECT p FROM Payment p WHERE p.paymentMethod.id = :methodId")
        Page<Payment> findByPaymentMethodId(@Param("methodId") Long methodId, Pageable pageable);

        @Query("SELECT p FROM Payment p WHERE p.receivedBy.id = :userId")
        Page<Payment> findByReceivedById(@Param("userId") Long userId, Pageable pageable);

        @Query("SELECT p FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate")
        Page<Payment> findByPaymentDateRange(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate,
                        Pageable pageable);

        @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.invoice.id = :invoiceId")
        BigDecimal sumPaymentsByInvoiceId(@Param("invoiceId") Long invoiceId);

        @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate")
        BigDecimal sumPaymentsByDateRange(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("SELECT COUNT(p) FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate")
        Long countPaymentsByDateRange(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("SELECT p.paymentMethod.name, SUM(p.amount) FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate GROUP BY p.paymentMethod.name")
        List<Object[]> sumPaymentsByMethodAndDateRange(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);
}