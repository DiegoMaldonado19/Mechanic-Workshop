package com.project.ayd.mechanic_workshop.features.workorders.repository;

import com.project.ayd.mechanic_workshop.features.workorders.entity.Quotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation, Long> {

    @Query("SELECT q FROM Quotation q LEFT JOIN FETCH q.createdBy WHERE q.work.id = :workId ORDER BY q.createdAt DESC")
    List<Quotation> findByWorkIdOrderByCreatedAtDesc(@Param("workId") Long workId);

    @Query("SELECT q FROM Quotation q WHERE q.work.id = :workId AND q.clientApproved = true ORDER BY q.createdAt DESC")
    Optional<Quotation> findApprovedQuotationByWorkId(@Param("workId") Long workId);

    @Query("SELECT q FROM Quotation q WHERE q.work.id = :workId ORDER BY q.createdAt DESC")
    Optional<Quotation> findLatestQuotationByWorkId(@Param("workId") Long workId);

    @Query("SELECT q FROM Quotation q WHERE q.clientApproved = :approved ORDER BY q.createdAt DESC")
    List<Quotation> findByClientApprovedOrderByCreatedAtDesc(@Param("approved") Boolean approved);

    @Query("SELECT q FROM Quotation q WHERE q.validUntil < :currentDate AND q.clientApproved = false")
    List<Quotation> findExpiredQuotations(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT q FROM Quotation q WHERE q.validUntil BETWEEN :startDate AND :endDate AND q.clientApproved = false")
    List<Quotation> findQuotationsExpiringBetween(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(q) FROM Quotation q WHERE q.work.id = :workId")
    Long countByWorkId(@Param("workId") Long workId);

    @Query("SELECT q FROM Quotation q WHERE q.createdBy.id = :userId ORDER BY q.createdAt DESC")
    List<Quotation> findByCreatedByIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("SELECT COUNT(q) FROM Quotation q WHERE q.clientApproved = true")
    Long countApprovedQuotations();

    @Query("SELECT COUNT(q) FROM Quotation q WHERE q.clientApproved = false")
    Long countPendingQuotations();
}