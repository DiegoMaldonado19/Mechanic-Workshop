package com.project.ayd.mechanic_workshop.features.inventory.service;

import com.project.ayd.mechanic_workshop.features.inventory.dto.MovementTypeResponse;
import com.project.ayd.mechanic_workshop.features.inventory.dto.ReferenceTypeResponse;
import com.project.ayd.mechanic_workshop.features.inventory.dto.StockMovementRequest;
import com.project.ayd.mechanic_workshop.features.inventory.dto.StockMovementResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface StockMovementService {

    StockMovementResponse recordMovement(StockMovementRequest request);

    Page<StockMovementResponse> getAllMovements(Pageable pageable);

    Page<StockMovementResponse> getMovementsByPart(Long partId, Pageable pageable);

    Page<StockMovementResponse> getMovementsByType(Long movementTypeId, Pageable pageable);

    Page<StockMovementResponse> getMovementsByUser(Long userId, Pageable pageable);

    Page<StockMovementResponse> getMovementsByDateRange(LocalDateTime startDate, LocalDateTime endDate,
            Pageable pageable);

    List<StockMovementResponse> getMovementsByReference(String referenceTypeName, Long referenceId);

    List<MovementTypeResponse> getAllMovementTypes();

    List<ReferenceTypeResponse> getAllReferenceTypes();
}