package com.project.ayd.mechanic_workshop.features.workorders.repository;

import com.project.ayd.mechanic_workshop.features.workorders.entity.WorkPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkPartRepository extends JpaRepository<WorkPart, Long> {

    @Query("SELECT wp FROM WorkPart wp LEFT JOIN FETCH wp.part p LEFT JOIN FETCH p.category LEFT JOIN FETCH wp.requestedBy LEFT JOIN FETCH wp.approvedBy WHERE wp.work.id = :workId ORDER BY wp.createdAt ASC")
    List<WorkPart> findByWorkIdWithDetails(@Param("workId") Long workId);

    @Query("SELECT wp FROM WorkPart wp WHERE wp.work.id = :workId AND wp.part.id = :partId")
    Optional<WorkPart> findByWorkIdAndPartId(@Param("workId") Long workId, @Param("partId") Long partId);

    @Query("SELECT wp FROM WorkPart wp WHERE wp.requestedBy.id = :userId ORDER BY wp.createdAt DESC")
    List<WorkPart> findByRequestedByIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("SELECT wp FROM WorkPart wp WHERE wp.approvedBy IS NULL ORDER BY wp.createdAt ASC")
    List<WorkPart> findPendingApprovalOrderByCreatedAtAsc();

    @Query("SELECT COALESCE(SUM(wp.unitPrice * wp.quantityUsed), 0) FROM WorkPart wp WHERE wp.work.id = :workId")
    BigDecimal calculateTotalPartsCostByWorkId(@Param("workId") Long workId);

    @Query("SELECT COUNT(wp) FROM WorkPart wp WHERE wp.work.id = :workId")
    Long countByWorkId(@Param("workId") Long workId);

    @Query("SELECT wp FROM WorkPart wp WHERE wp.part.id = :partId ORDER BY wp.createdAt DESC")
    List<WorkPart> findByPartIdOrderByCreatedAtDesc(@Param("partId") Long partId);

    @Query("SELECT COALESCE(SUM(wp.quantityUsed), 0) FROM WorkPart wp WHERE wp.part.id = :partId")
    Integer sumQuantityUsedByPartId(@Param("partId") Long partId);
}