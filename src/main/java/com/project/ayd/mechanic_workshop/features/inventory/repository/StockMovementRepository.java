package com.project.ayd.mechanic_workshop.features.inventory.repository;

import com.project.ayd.mechanic_workshop.features.inventory.entity.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    Page<StockMovement> findByPartId(Long partId, Pageable pageable);

    Page<StockMovement> findByMovementTypeId(Long movementTypeId, Pageable pageable);

    Page<StockMovement> findByCreatedById(Long userId, Pageable pageable);

    @Query("SELECT sm FROM StockMovement sm WHERE sm.createdAt BETWEEN :startDate AND :endDate")
    Page<StockMovement> findByDateRange(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("SELECT sm FROM StockMovement sm WHERE sm.part.id = :partId AND sm.createdAt BETWEEN :startDate AND :endDate")
    List<StockMovement> findByPartIdAndDateRange(@Param("partId") Long partId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT sm FROM StockMovement sm WHERE sm.referenceType.name = :referenceTypeName AND sm.referenceId = :referenceId")
    List<StockMovement> findByReferenceTypeAndReferenceId(@Param("referenceTypeName") String referenceTypeName,
            @Param("referenceId") Long referenceId);
}