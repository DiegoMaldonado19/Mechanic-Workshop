package com.project.ayd.mechanic_workshop.features.billing.repository;

import com.project.ayd.mechanic_workshop.features.billing.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByWorkId(Long workId);

    @Query("SELECT i FROM Invoice i WHERE i.work.vehicle.ownerCui = :clientCui")
    Page<Invoice> findByClientCui(@Param("clientCui") String clientCui, Pageable pageable);

    @Query("SELECT i FROM Invoice i WHERE i.paymentStatus.id = :statusId")
    Page<Invoice> findByPaymentStatusId(@Param("statusId") Long statusId, Pageable pageable);

    @Query("SELECT i FROM Invoice i WHERE i.paymentStatus.name = :statusName")
    Page<Invoice> findByPaymentStatusName(@Param("statusName") String statusName, Pageable pageable);

    @Query("SELECT i FROM Invoice i WHERE i.dueDate < :date AND i.paymentStatus.name != 'Pagado'")
    List<Invoice> findOverdueInvoices(@Param("date") LocalDate date);

    @Query("SELECT i FROM Invoice i WHERE i.issuedDate BETWEEN :startDate AND :endDate")
    Page<Invoice> findByIssuedDateRange(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    @Query("SELECT i FROM Invoice i WHERE i.createdBy.id = :userId")
    Page<Invoice> findByCreatedById(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.paymentStatus.name = 'Pagado' AND i.issuedDate BETWEEN :startDate AND :endDate")
    BigDecimal sumPaidInvoicesByDateRange(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.paymentStatus.name = 'Pendiente' AND i.dueDate < :date")
    BigDecimal sumOverdueInvoicesAmount(@Param("date") LocalDate date);

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.issuedDate BETWEEN :startDate AND :endDate")
    Long countInvoicesByDateRange(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}