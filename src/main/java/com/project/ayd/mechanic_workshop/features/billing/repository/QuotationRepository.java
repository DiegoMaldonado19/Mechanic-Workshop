package com.project.ayd.mechanic_workshop.features.billing.repository;

import com.project.ayd.mechanic_workshop.features.billing.entity.Quotation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation, Long> {

    Optional<Quotation> findByWorkId(Long workId);

    @Query("SELECT q FROM Quotation q WHERE q.work.vehicle.ownerCui = :clientCui")
    Page<Quotation> findByClientCui(@Param("clientCui") String clientCui, Pageable pageable);

    @Query("SELECT q FROM Quotation q WHERE q.clientApproved = :approved")
    Page<Quotation> findByClientApproved(@Param("approved") Boolean approved, Pageable pageable);

    @Query("SELECT q FROM Quotation q WHERE q.validUntil < :date AND q.clientApproved = false")
    List<Quotation> findExpiredQuotations(@Param("date") LocalDate date);

    @Query("SELECT q FROM Quotation q WHERE q.createdAt BETWEEN :startDate AND :endDate")
    Page<Quotation> findByDateRange(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("SELECT q FROM Quotation q WHERE q.createdBy.id = :userId")
    Page<Quotation> findByCreatedById(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(q) FROM Quotation q WHERE q.clientApproved = true AND q.createdAt BETWEEN :startDate AND :endDate")
    Long countApprovedQuotationsByDateRange(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(q.totalAmount) FROM Quotation q WHERE q.clientApproved = true AND q.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal sumApprovedQuotationsByDateRange(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}