package com.project.ayd.mechanic_workshop.features.inventory.repository;

import com.project.ayd.mechanic_workshop.features.inventory.entity.PurchaseOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, Long> {

    List<PurchaseOrderItem> findByPurchaseOrderId(Long purchaseOrderId);

    List<PurchaseOrderItem> findByPartId(Long partId);

    @Query("SELECT poi FROM PurchaseOrderItem poi WHERE poi.quantityOrdered > poi.quantityReceived")
    List<PurchaseOrderItem> findPendingItems();

    @Query("SELECT SUM(poi.quantityOrdered - poi.quantityReceived) FROM PurchaseOrderItem poi WHERE poi.part.id = :partId")
    Integer getPendingQuantityByPart(@Param("partId") Long partId);
}