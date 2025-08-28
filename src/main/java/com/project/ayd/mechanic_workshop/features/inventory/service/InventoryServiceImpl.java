package com.project.ayd.mechanic_workshop.features.inventory.service;

import com.project.ayd.mechanic_workshop.features.auth.entity.User;
import com.project.ayd.mechanic_workshop.features.auth.repository.UserRepository;
import com.project.ayd.mechanic_workshop.features.inventory.dto.*;
import com.project.ayd.mechanic_workshop.features.inventory.entity.InventoryStock;
import com.project.ayd.mechanic_workshop.features.inventory.entity.MovementTypeEntity;
import com.project.ayd.mechanic_workshop.features.inventory.entity.ReferenceTypeEntity;
import com.project.ayd.mechanic_workshop.features.inventory.entity.StockMovement;
import com.project.ayd.mechanic_workshop.features.inventory.repository.InventoryStockRepository;
import com.project.ayd.mechanic_workshop.features.inventory.repository.MovementTypeRepository;
import com.project.ayd.mechanic_workshop.features.inventory.repository.ReferenceTypeRepository;
import com.project.ayd.mechanic_workshop.features.inventory.repository.StockMovementRepository;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

        private final InventoryStockRepository inventoryStockRepository;
        private final StockMovementRepository stockMovementRepository;
        private final PartRepository partRepository;
        private final UserRepository userRepository;
        private final MovementTypeRepository movementTypeRepository;
        private final ReferenceTypeRepository referenceTypeRepository;

        @Override
        @Transactional(readOnly = true)
        public InventoryStockResponse getStockByPartId(Long partId) {
                InventoryStock stock = inventoryStockRepository.findByPartId(partId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Stock not found for part ID: " + partId));
                return mapToInventoryStockResponse(stock);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<InventoryStockResponse> getAllStocks(Pageable pageable) {
                return inventoryStockRepository.findAll(pageable)
                                .map(this::mapToInventoryStockResponse);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<InventoryStockResponse> searchStocksByPartName(String searchTerm, Pageable pageable) {
                return inventoryStockRepository.findByPartNameOrDescriptionContaining(searchTerm, pageable)
                                .map(this::mapToInventoryStockResponse);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<InventoryStockResponse> getStocksByCategory(Long categoryId, Pageable pageable) {
                return inventoryStockRepository.findByPartCategoryId(categoryId, pageable)
                                .map(this::mapToInventoryStockResponse);
        }

        @Override
        @Transactional(readOnly = true)
        public List<InventoryStockResponse> getLowStockItems() {
                return inventoryStockRepository.findLowStockItems()
                                .stream()
                                .map(this::mapToInventoryStockResponse)
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public List<InventoryStockResponse> getOutOfStockItems() {
                return inventoryStockRepository.findOutOfStockItems()
                                .stream()
                                .map(this::mapToInventoryStockResponse)
                                .toList();
        }

        @Override
        @Transactional
        public InventoryStockResponse adjustStock(StockAdjustmentRequest request) {
                InventoryStock stock = inventoryStockRepository.findByPartId(request.getPartId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Stock not found for part ID: " + request.getPartId()));

                int previousQuantity = stock.getQuantityAvailable();
                int quantityDifference = request.getNewQuantity() - previousQuantity;

                stock.setQuantityAvailable(request.getNewQuantity());
                stock.setLastRestocked(LocalDateTime.now());
                stock = inventoryStockRepository.save(stock);

                recordStockMovement(request.getPartId(), quantityDifference, "Ajuste", "Ajuste",
                                null, request.getReason());

                log.info("Stock adjusted for part ID {}: {} -> {}", request.getPartId(),
                                previousQuantity, request.getNewQuantity());

                return mapToInventoryStockResponse(stock);
        }

        @Override
        @Transactional
        public InventoryStockResponse addStock(InventoryStockRequest request) {
                InventoryStock stock = inventoryStockRepository.findByPartId(request.getPartId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Stock not found for part ID: " + request.getPartId()));

                stock.setQuantityAvailable(stock.getQuantityAvailable() + request.getQuantity());
                stock.setLastRestocked(LocalDateTime.now());
                stock = inventoryStockRepository.save(stock);

                recordStockMovement(request.getPartId(), request.getQuantity(), "Entrada",
                                "Inventario inicial", null, request.getNotes());

                log.info("Stock added for part ID {}: +{}", request.getPartId(), request.getQuantity());

                return mapToInventoryStockResponse(stock);
        }

        @Override
        @Transactional
        public InventoryStockResponse removeStock(InventoryStockRequest request) {
                InventoryStock stock = inventoryStockRepository.findByPartId(request.getPartId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Stock not found for part ID: " + request.getPartId()));

                if (stock.getQuantityAvailable() < request.getQuantity()) {
                        throw new IllegalArgumentException("Insufficient stock available");
                }

                stock.setQuantityAvailable(stock.getQuantityAvailable() - request.getQuantity());
                stock = inventoryStockRepository.save(stock);

                recordStockMovement(request.getPartId(), -request.getQuantity(), "Salida",
                                "Ajuste", null, request.getNotes());

                log.info("Stock removed for part ID {}: -{}", request.getPartId(), request.getQuantity());

                return mapToInventoryStockResponse(stock);
        }

        @Override
        @Transactional(readOnly = true)
        public InventoryReportResponse getInventoryReport() {
                Long totalParts = partRepository.count();
                Long totalStockQuantity = inventoryStockRepository.getTotalStockQuantity();
                Long lowStockCount = inventoryStockRepository.countLowStockItems();
                Long outOfStockCount = inventoryStockRepository.countOutOfStockItems();

                BigDecimal totalInventoryValue = calculateTotalInventoryValue();

                return InventoryReportResponse.builder()
                                .totalParts(totalParts)
                                .totalStockQuantity(totalStockQuantity != null ? totalStockQuantity : 0L)
                                .lowStockCount(lowStockCount)
                                .outOfStockCount(outOfStockCount)
                                .totalInventoryValue(totalInventoryValue)
                                .build();
        }

        @Override
        @Transactional
        public boolean reserveStock(Long partId, Integer quantity) {
                InventoryStock stock = inventoryStockRepository.findByPartId(partId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Stock not found for part ID: " + partId));

                if (stock.getQuantityAvailable() < quantity) {
                        return false;
                }

                stock.setQuantityAvailable(stock.getQuantityAvailable() - quantity);
                stock.setQuantityReserved(stock.getQuantityReserved() + quantity);
                inventoryStockRepository.save(stock);

                log.info("Stock reserved for part ID {}: {}", partId, quantity);
                return true;
        }

        @Override
        @Transactional
        public boolean releaseReservedStock(Long partId, Integer quantity) {
                InventoryStock stock = inventoryStockRepository.findByPartId(partId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Stock not found for part ID: " + partId));

                if (stock.getQuantityReserved() < quantity) {
                        return false;
                }

                stock.setQuantityReserved(stock.getQuantityReserved() - quantity);
                stock.setQuantityAvailable(stock.getQuantityAvailable() + quantity);
                inventoryStockRepository.save(stock);

                log.info("Reserved stock released for part ID {}: {}", partId, quantity);
                return true;
        }

        @Override
        @Transactional
        public boolean confirmStockUsage(Long partId, Integer quantity) {
                InventoryStock stock = inventoryStockRepository.findByPartId(partId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Stock not found for part ID: " + partId));

                if (stock.getQuantityReserved() < quantity) {
                        return false;
                }

                stock.setQuantityReserved(stock.getQuantityReserved() - quantity);
                inventoryStockRepository.save(stock);

                recordStockMovement(partId, -quantity, "Salida", "Trabajo", null,
                                "Confirmación de uso de stock");

                log.info("Stock usage confirmed for part ID {}: {}", partId, quantity);
                return true;
        }

        private void recordStockMovement(Long partId, Integer quantity, String movementTypeName,
                        String referenceTypeName, Long referenceId, String notes) {
                Part part = partRepository.findById(partId)
                                .orElseThrow(() -> new IllegalArgumentException("Part not found with ID: " + partId));

                MovementTypeEntity movementType = movementTypeRepository.findByName(movementTypeName)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Movement type not found: " + movementTypeName));

                ReferenceTypeEntity referenceType = referenceTypeRepository.findByName(referenceTypeName)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Reference type not found: " + referenceTypeName));

                User currentUser = getCurrentUser();

                StockMovement movement = StockMovement.builder()
                                .part(part)
                                .movementType(movementType)
                                .quantity(quantity)
                                .referenceType(referenceType)
                                .referenceId(referenceId)
                                .notes(notes)
                                .createdBy(currentUser)
                                .build();

                stockMovementRepository.save(movement);
        }

        private BigDecimal calculateTotalInventoryValue() {
                List<InventoryStock> allStocks = inventoryStockRepository.findAll();
                return allStocks.stream()
                                .map(stock -> stock.getPart().getUnitPrice()
                                                .multiply(BigDecimal.valueOf(stock.getQuantityAvailable())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        private User getCurrentUser() {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                return userRepository.findByUsername(username)
                                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        }

        private InventoryStockResponse mapToInventoryStockResponse(InventoryStock stock) {
                boolean isLowStock = stock.getQuantityAvailable() < stock.getPart().getMinimumStock();
                boolean isOutOfStock = stock.getQuantityAvailable() == 0;

                return InventoryStockResponse.builder()
                                .id(stock.getId())
                                .part(mapToPartResponse(stock.getPart()))
                                .quantityAvailable(stock.getQuantityAvailable())
                                .quantityReserved(stock.getQuantityReserved())
                                .lastRestocked(stock.getLastRestocked())
                                .createdAt(stock.getCreatedAt())
                                .updatedAt(stock.getUpdatedAt())
                                .isLowStock(isLowStock)
                                .isOutOfStock(isOutOfStock)
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

}