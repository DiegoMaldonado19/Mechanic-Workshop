package com.project.ayd.mechanic_workshop.features.inventory.repository;

import com.project.ayd.mechanic_workshop.features.inventory.entity.PurchaseOrderStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PurchaseOrderStatusRepository extends JpaRepository<PurchaseOrderStatusEntity, Long> {
    Optional<PurchaseOrderStatusEntity> findByName(String name);
}