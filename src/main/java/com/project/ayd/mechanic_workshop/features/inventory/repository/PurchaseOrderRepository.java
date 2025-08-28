package com.project.ayd.mechanic_workshop.features.inventory.repository;

import com.project.ayd.mechanic_workshop.features.inventory.entity.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    Page<PurchaseOrder> findBySupplierId(Long supplierId, Pageable pageable);

    Page<PurchaseOrder> findByStatusId(Long statusId, Pageable pageable);

    Page<PurchaseOrder> findByCreatedById(Long userId, Pageable pageable);

    @Query("SELECT po FROM PurchaseOrder po WHERE po.orderDate BETWEEN :startDate AND :endDate")
    Page<PurchaseOrder> findByOrderDateBetween(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    @Query("SELECT po FROM PurchaseOrder po WHERE po.status.name = :statusName")
    List<PurchaseOrder> findByStatusName(@Param("statusName") String statusName);

    @Query("SELECT COUNT(po) FROM PurchaseOrder po WHERE po.status.name = :statusName")
    Long countByStatusName(@Param("statusName") String statusName);

    @Query("SELECT SUM(po.totalAmount) FROM PurchaseOrder po WHERE po.orderDate BETWEEN :startDate AND :endDate AND po.status.name = 'Entregada'")
    Double getTotalPurchaseAmountInPeriod(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}