package com.project.ayd.mechanic_workshop.features.workorders.repository;

import com.project.ayd.mechanic_workshop.features.workorders.entity.WorkOrderQuotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkOrderQuotationRepository extends JpaRepository<WorkOrderQuotation, Long> {

    @Query("SELECT q FROM WorkOrderQuotation q LEFT JOIN FETCH q.createdBy WHERE q.work.id = :workId ORDER BY q.createdAt DESC")
    List<WorkOrderQuotation> findByWorkIdOrderByCreatedAtDesc(@Param("workId") Long workId);

    @Query("SELECT q FROM WorkOrderQuotation q WHERE q.work.id = :workId AND q.clientApproved = true ORDER BY q.createdAt DESC")
    Optional<WorkOrderQuotation> findApprovedQuotationByWorkId(@Param("workId") Long workId);

    @Query("SELECT q FROM WorkOrderQuotation q WHERE q.work.id = :workId ORDER BY q.createdAt DESC")
    Optional<WorkOrderQuotation> findLatestQuotationByWorkId(@Param("workId") Long workId);

    @Query("SELECT q FROM WorkOrderQuotation q WHERE q.clientApproved = :approved ORDER BY q.createdAt DESC")
    List<WorkOrderQuotation> findByClientApprovedOrderByCreatedAtDesc(@Param("approved") Boolean approved);

    @Query("SELECT q FROM WorkOrderQuotation q WHERE q.validUntil < :currentDate AND q.clientApproved = false")
    List<WorkOrderQuotation> findExpiredQuotations(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT q FROM WorkOrderQuotation q WHERE q.validUntil BETWEEN :startDate AND :endDate AND q.clientApproved = false")
    List<WorkOrderQuotation> findQuotationsExpiringBetween(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(q) FROM WorkOrderQuotation q WHERE q.work.id = :workId")
    Long countByWorkId(@Param("workId") Long workId);

    @Query("SELECT q FROM WorkOrderQuotation q WHERE q.createdBy.id = :userId ORDER BY q.createdAt DESC")
    List<WorkOrderQuotation> findByCreatedByIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("SELECT COUNT(q) FROM WorkOrderQuotation q WHERE q.clientApproved = true")
    Long countApprovedQuotations();

    @Query("SELECT COUNT(q) FROM WorkOrderQuotation q WHERE q.clientApproved = false")
    Long countPendingQuotations();
}