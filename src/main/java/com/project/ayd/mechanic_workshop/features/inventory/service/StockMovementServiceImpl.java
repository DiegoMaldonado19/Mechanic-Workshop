package com.project.ayd.mechanic_workshop.features.inventory.service;

import com.project.ayd.mechanic_workshop.features.auth.entity.User;
import com.project.ayd.mechanic_workshop.features.auth.repository.UserRepository;
import com.project.ayd.mechanic_workshop.features.inventory.dto.*;
import com.project.ayd.mechanic_workshop.features.inventory.entity.MovementTypeEntity;
import com.project.ayd.mechanic_workshop.features.inventory.entity.ReferenceTypeEntity;
import com.project.ayd.mechanic_workshop.features.inventory.entity.StockMovement;
import com.project.ayd.mechanic_workshop.features.inventory.repository.MovementTypeRepository;
import com.project.ayd.mechanic_workshop.features.inventory.repository.ReferenceTypeRepository;
import com.project.ayd.mechanic_workshop.features.inventory.repository.StockMovementRepository;
import com.project.ayd.mechanic_workshop.features.users.dto.UserResponse;
import com.project.ayd.mechanic_workshop.features.workorders.entity.Part;
import com.project.ayd.mechanic_workshop.features.workorders.repository.PartRepository;
import com.project.ayd.mechanic_workshop.features.workorders.dto.PartResponse;
import com.project.ayd.mechanic_workshop.features.workorders.dto.PartCategoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockMovementServiceImpl implements StockMovementService {

        private final StockMovementRepository stockMovementRepository;
        private final PartRepository partRepository;
        private final MovementTypeRepository movementTypeRepository;
        private final ReferenceTypeRepository referenceTypeRepository;
        private final UserRepository userRepository;

        @Override
        @Transactional
        public StockMovementResponse recordMovement(StockMovementRequest request) {
                Part part = partRepository.findById(request.getPartId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Part not found with ID: " + request.getPartId()));

                MovementTypeEntity movementType = movementTypeRepository.findById(request.getMovementTypeId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Movement type not found with ID: " + request.getMovementTypeId()));

                ReferenceTypeEntity referenceType = null;
                if (request.getReferenceTypeId() != null) {
                        referenceType = referenceTypeRepository.findById(request.getReferenceTypeId())
                                        .orElseThrow(() -> new IllegalArgumentException(
                                                        "Reference type not found with ID: "
                                                                        + request.getReferenceTypeId()));
                }

                User currentUser = getCurrentUser();

                StockMovement movement = StockMovement.builder()
                                .part(part)
                                .movementType(movementType)
                                .quantity(request.getQuantity())
                                .referenceType(referenceType)
                                .referenceId(request.getReferenceId())
                                .notes(request.getNotes())
                                .createdBy(currentUser)
                                .build();

                movement = stockMovementRepository.save(movement);

                log.info("Stock movement recorded for part ID {}: {} units", request.getPartId(),
                                request.getQuantity());

                return mapToStockMovementResponse(movement);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<StockMovementResponse> getAllMovements(Pageable pageable) {
                return stockMovementRepository.findAll(pageable)
                                .map(this::mapToStockMovementResponse);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<StockMovementResponse> getMovementsByPart(Long partId, Pageable pageable) {
                return stockMovementRepository.findByPartId(partId, pageable)
                                .map(this::mapToStockMovementResponse);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<StockMovementResponse> getMovementsByType(Long movementTypeId, Pageable pageable) {
                return stockMovementRepository.findByMovementTypeId(movementTypeId, pageable)
                                .map(this::mapToStockMovementResponse);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<StockMovementResponse> getMovementsByUser(Long userId, Pageable pageable) {
                return stockMovementRepository.findByCreatedById(userId, pageable)
                                .map(this::mapToStockMovementResponse);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<StockMovementResponse> getMovementsByDateRange(LocalDateTime startDate, LocalDateTime endDate,
                        Pageable pageable) {
                return stockMovementRepository.findByDateRange(startDate, endDate, pageable)
                                .map(this::mapToStockMovementResponse);
        }

        @Override
        @Transactional(readOnly = true)
        public List<StockMovementResponse> getMovementsByReference(String referenceTypeName, Long referenceId) {
                return stockMovementRepository.findByReferenceTypeAndReferenceId(referenceTypeName, referenceId)
                                .stream()
                                .map(this::mapToStockMovementResponse)
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public List<MovementTypeResponse> getAllMovementTypes() {
                return movementTypeRepository.findAll()
                                .stream()
                                .map(this::mapToMovementTypeResponse)
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public List<ReferenceTypeResponse> getAllReferenceTypes() {
                return referenceTypeRepository.findAll()
                                .stream()
                                .map(this::mapToReferenceTypeResponse)
                                .toList();
        }

        private User getCurrentUser() {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                return userRepository.findByUsername(username)
                                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        }

        private StockMovementResponse mapToStockMovementResponse(StockMovement movement) {
                return StockMovementResponse.builder()
                                .id(movement.getId())
                                .part(mapToPartResponse(movement.getPart()))
                                .movementType(mapToMovementTypeResponse(movement.getMovementType()))
                                .quantity(movement.getQuantity())
                                .referenceType(movement.getReferenceType() != null
                                                ? mapToReferenceTypeResponse(movement.getReferenceType())
                                                : null)
                                .referenceId(movement.getReferenceId())
                                .notes(movement.getNotes())
                                .createdBy(mapToUserResponse(movement.getCreatedBy()))
                                .createdAt(movement.getCreatedAt())
                                .build();
        }

        private MovementTypeResponse mapToMovementTypeResponse(MovementTypeEntity movementType) {
                return MovementTypeResponse.builder()
                                .id(movementType.getId())
                                .name(movementType.getName())
                                .description(movementType.getDescription())
                                .createdAt(movementType.getCreatedAt())
                                .build();
        }

        private ReferenceTypeResponse mapToReferenceTypeResponse(ReferenceTypeEntity referenceType) {
                return ReferenceTypeResponse.builder()
                                .id(referenceType.getId())
                                .name(referenceType.getName())
                                .description(referenceType.getDescription())
                                .createdAt(referenceType.getCreatedAt())
                                .build();
        }

        // Método de mapeo local para Part
        private PartResponse mapToPartResponse(Part part) {
                PartCategoryResponse categoryResponse = null;
                if (part.getCategory() != null) {
                        categoryResponse = PartCategoryResponse.builder()
                                        .id(part.getCategory().getId())
                                        .name(part.getCategory().getName())
                                        .description(part.getCategory().getDescription())
                                        .createdAt(part.getCategory().getCreatedAt())
                                        .build();
                }

                return PartResponse.builder()
                                .id(part.getId())
                                .name(part.getName())
                                .description(part.getDescription())
                                .category(categoryResponse)
                                .unitPrice(part.getUnitPrice())
                                .minimumStock(part.getMinimumStock())
                                .createdAt(part.getCreatedAt())
                                .updatedAt(part.getUpdatedAt())
                                .build();
        }

        // Método de mapeo local para User
        private UserResponse mapToUserResponse(User user) {
                return UserResponse.builder()
                                .id(user.getId())
                                .cui(user.getPerson().getCui())
                                .nit(user.getPerson().getNit())
                                .firstName(user.getPerson().getFirstName())
                                .lastName(user.getPerson().getLastName())
                                .email(user.getPerson().getEmail())
                                .phone(user.getPerson().getPhone())
                                .username(user.getUsername())
                                .userType(user.getUserType().getName())
                                .isActive(user.getIsActive())
                                .createdAt(user.getCreatedAt())
                                .build();
        }
}