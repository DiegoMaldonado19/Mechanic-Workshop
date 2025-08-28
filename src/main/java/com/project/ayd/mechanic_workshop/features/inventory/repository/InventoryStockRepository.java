package com.project.ayd.mechanic_workshop.features.inventory.repository;

import com.project.ayd.mechanic_workshop.features.inventory.entity.InventoryStock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryStockRepository extends JpaRepository<InventoryStock, Long> {

    Optional<InventoryStock> findByPartId(Long partId);

    @Query("SELECT is FROM InventoryStock is WHERE is.quantityAvailable < is.part.minimumStock")
    List<InventoryStock> findLowStockItems();

    @Query("SELECT is FROM InventoryStock is WHERE is.quantityAvailable = 0")
    List<InventoryStock> findOutOfStockItems();

    @Query("SELECT is FROM InventoryStock is WHERE is.part.name ILIKE %:searchTerm% OR is.part.description ILIKE %:searchTerm%")
    Page<InventoryStock> findByPartNameOrDescriptionContaining(@Param("searchTerm") String searchTerm,
            Pageable pageable);

    @Query("SELECT is FROM InventoryStock is WHERE is.part.category.id = :categoryId")
    Page<InventoryStock> findByPartCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT SUM(is.quantityAvailable) FROM InventoryStock is")
    Long getTotalStockQuantity();

    @Query("SELECT COUNT(is) FROM InventoryStock is WHERE is.quantityAvailable < is.part.minimumStock")
    Long countLowStockItems();

    @Query("SELECT COUNT(is) FROM InventoryStock is WHERE is.quantityAvailable = 0")
    Long countOutOfStockItems();
}