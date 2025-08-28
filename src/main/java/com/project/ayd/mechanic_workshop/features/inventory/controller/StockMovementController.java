package com.project.ayd.mechanic_workshop.features.inventory.controller;

import com.project.ayd.mechanic_workshop.features.inventory.dto.*;
import com.project.ayd.mechanic_workshop.features.inventory.service.StockMovementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/inventory/movements")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO')")
public class StockMovementController {

    private final StockMovementService stockMovementService;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<StockMovementResponse> recordMovement(@Valid @RequestBody StockMovementRequest request) {
        StockMovementResponse response = stockMovementService.recordMovement(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<StockMovementResponse>> getAllMovements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<StockMovementResponse> response = stockMovementService.getAllMovements(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/part/{partId}")
    public ResponseEntity<Page<StockMovementResponse>> getMovementsByPart(
            @PathVariable Long partId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<StockMovementResponse> response = stockMovementService.getMovementsByPart(partId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{movementTypeId}")
    public ResponseEntity<Page<StockMovementResponse>> getMovementsByType(
            @PathVariable Long movementTypeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<StockMovementResponse> response = stockMovementService.getMovementsByType(movementTypeId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or #userId == authentication.principal.id")
    public ResponseEntity<Page<StockMovementResponse>> getMovementsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<StockMovementResponse> response = stockMovementService.getMovementsByUser(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date-range")
    public ResponseEntity<Page<StockMovementResponse>> getMovementsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<StockMovementResponse> response = stockMovementService.getMovementsByDateRange(startDate, endDate,
                pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reference/{referenceTypeName}/{referenceId}")
    public ResponseEntity<List<StockMovementResponse>> getMovementsByReference(
            @PathVariable String referenceTypeName,
            @PathVariable Long referenceId) {

        List<StockMovementResponse> response = stockMovementService.getMovementsByReference(referenceTypeName,
                referenceId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/movement-types")
    public ResponseEntity<List<MovementTypeResponse>> getAllMovementTypes() {
        List<MovementTypeResponse> response = stockMovementService.getAllMovementTypes();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reference-types")
    public ResponseEntity<List<ReferenceTypeResponse>> getAllReferenceTypes() {
        List<ReferenceTypeResponse> response = stockMovementService.getAllReferenceTypes();
        return ResponseEntity.ok(response);
    }
}