package com.project.ayd.mechanic_workshop.features.inventory.service;

import com.project.ayd.mechanic_workshop.features.auth.entity.User;
import com.project.ayd.mechanic_workshop.features.auth.repository.UserRepository;
import com.project.ayd.mechanic_workshop.features.inventory.dto.*;
import com.project.ayd.mechanic_workshop.features.inventory.entity.*;
import com.project.ayd.mechanic_workshop.features.inventory.enums.PurchaseOrderStatus;
import com.project.ayd.mechanic_workshop.features.inventory.repository.*;
import com.project.ayd.mechanic_workshop.features.workorders.entity.Part;
import com.project.ayd.mechanic_workshop.features.workorders.repository.PartRepository;
import com.project.ayd.mechanic_workshop.features.workorders.dto.PartResponse;
import com.project.ayd.mechanic_workshop.features.workorders.dto.PartCategoryResponse;
import com.project.ayd.mechanic_workshop.features.users.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

        private final PurchaseOrderRepository purchaseOrderRepository;
        private final PurchaseOrderItemRepository purchaseOrderItemRepository;
        private final SupplierRepository supplierRepository;
        private final PurchaseOrderStatusRepository purchaseOrderStatusRepository;
        private final PartRepository partRepository;
        private final UserRepository userRepository;
        private final InventoryService inventoryService;

        @Override
        @Transactional
        public PurchaseOrderResponse createPurchaseOrder(PurchaseOrderRequest request) {
                Supplier supplier = supplierRepository.findById(request.getSupplierId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Supplier not found with ID: " + request.getSupplierId()));

                PurchaseOrderStatusEntity pendingStatus = purchaseOrderStatusRepository
                                .findByName(PurchaseOrderStatus.PENDIENTE.getDisplayName())
                                .orElseThrow(() -> new IllegalArgumentException("Pending status not found"));

                User currentUser = getCurrentUser();

                BigDecimal totalAmount = calculateTotalAmount(request.getItems());

                PurchaseOrder purchaseOrder = PurchaseOrder.builder()
                                .supplier(supplier)
                                .totalAmount(totalAmount)
                                .expectedDeliveryDate(request.getExpectedDeliveryDate())
                                .status(pendingStatus)
                                .createdBy(currentUser)
                                .items(new ArrayList<>())
                                .build();

                purchaseOrder = purchaseOrderRepository.save(purchaseOrder);

                List<PurchaseOrderItem> items = createPurchaseOrderItems(request.getItems(), purchaseOrder);
                purchaseOrder.setItems(items);

                log.info("Purchase order created successfully with ID: {}", purchaseOrder.getId());

                return mapToPurchaseOrderResponse(purchaseOrder);
        }

        @Override
        @Transactional(readOnly = true)
        public PurchaseOrderResponse getPurchaseOrderById(Long purchaseOrderId) {
                PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Purchase order not found with ID: " + purchaseOrderId));
                return mapToPurchaseOrderResponse(purchaseOrder);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<PurchaseOrderResponse> getAllPurchaseOrders(Pageable pageable) {
                return purchaseOrderRepository.findAll(pageable)
                                .map(this::mapToPurchaseOrderResponse);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<PurchaseOrderResponse> getPurchaseOrdersBySupplier(Long supplierId, Pageable pageable) {
                return purchaseOrderRepository.findBySupplierId(supplierId, pageable)
                                .map(this::mapToPurchaseOrderResponse);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<PurchaseOrderResponse> getPurchaseOrdersByStatus(Long statusId, Pageable pageable) {
                return purchaseOrderRepository.findByStatusId(statusId, pageable)
                                .map(this::mapToPurchaseOrderResponse);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<PurchaseOrderResponse> getPurchaseOrdersByDateRange(LocalDate startDate, LocalDate endDate,
                        Pageable pageable) {
                return purchaseOrderRepository.findByOrderDateBetween(startDate, endDate, pageable)
                                .map(this::mapToPurchaseOrderResponse);
        }

        @Override
        @Transactional
        public PurchaseOrderResponse updatePurchaseOrderStatus(Long purchaseOrderId, Long statusId) {
                PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Purchase order not found with ID: " + purchaseOrderId));

                PurchaseOrderStatusEntity status = purchaseOrderStatusRepository.findById(statusId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Purchase order status not found with ID: " + statusId));

                purchaseOrder.setStatus(status);
                purchaseOrder = purchaseOrderRepository.save(purchaseOrder);

                log.info("Purchase order status updated for ID {}: {}", purchaseOrderId, status.getName());

                return mapToPurchaseOrderResponse(purchaseOrder);
        }

        @Override
        @Transactional
        public PurchaseOrderResponse receivePurchaseOrderItem(Long purchaseOrderId, Long itemId,
                        Integer quantityReceived) {
                PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Purchase order not found with ID: " + purchaseOrderId));

                PurchaseOrderItem item = purchaseOrder.getItems().stream()
                                .filter(i -> i.getId().equals(itemId))
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Purchase order item not found with ID: " + itemId));

                int previouslyReceived = item.getQuantityReceived();
                int totalReceived = previouslyReceived + quantityReceived;

                if (totalReceived > item.getQuantityOrdered()) {
                        throw new IllegalArgumentException("Cannot receive more items than ordered");
                }

                item.setQuantityReceived(totalReceived);
                purchaseOrderItemRepository.save(item);

                InventoryStockRequest stockRequest = InventoryStockRequest.builder()
                                .partId(item.getPart().getId())
                                .quantity(quantityReceived)
                                .notes("Recepción de orden de compra #" + purchaseOrderId)
                                .build();

                inventoryService.addStock(stockRequest);

                log.info("Purchase order item received - Order: {}, Item: {}, Quantity: {}",
                                purchaseOrderId, itemId, quantityReceived);

                return mapToPurchaseOrderResponse(purchaseOrder);
        }

        @Override
        @Transactional
        public PurchaseOrderResponse completePurchaseOrderDelivery(Long purchaseOrderId) {
                PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Purchase order not found with ID: " + purchaseOrderId));

                PurchaseOrderStatusEntity deliveredStatus = purchaseOrderStatusRepository
                                .findByName(PurchaseOrderStatus.ENTREGADA.getDisplayName())
                                .orElseThrow(() -> new IllegalArgumentException("Delivered status not found"));

                purchaseOrder.setStatus(deliveredStatus);
                purchaseOrder.setActualDeliveryDate(LocalDate.now());
                purchaseOrder = purchaseOrderRepository.save(purchaseOrder);

                log.info("Purchase order delivery completed for ID: {}", purchaseOrderId);

                return mapToPurchaseOrderResponse(purchaseOrder);
        }

        @Override
        @Transactional(readOnly = true)
        public List<PurchaseOrderItemResponse> getPendingItems() {
                return purchaseOrderItemRepository.findPendingItems()
                                .stream()
                                .map(this::mapToPurchaseOrderItemResponse)
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public Integer getPendingQuantityForPart(Long partId) {
                Integer pendingQuantity = purchaseOrderItemRepository.getPendingQuantityByPart(partId);
                return pendingQuantity != null ? pendingQuantity : 0;
        }

        @Override
        @Transactional(readOnly = true)
        public List<PurchaseOrderStatusResponse> getAllPurchaseOrderStatuses() {
                return purchaseOrderStatusRepository.findAll()
                                .stream()
                                .map(this::mapToPurchaseOrderStatusResponse)
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public Long countPurchaseOrdersByStatus(String statusName) {
                return purchaseOrderRepository.countByStatusName(statusName);
        }

        @Override
        @Transactional(readOnly = true)
        public Double getTotalPurchaseAmountInPeriod(LocalDate startDate, LocalDate endDate) {
                Double amount = purchaseOrderRepository.getTotalPurchaseAmountInPeriod(startDate, endDate);
                return amount != null ? amount : 0.0;
        }

        private BigDecimal calculateTotalAmount(List<PurchaseOrderItemRequest> items) {
                return items.stream()
                                .map(item -> item.getUnitPrice()
                                                .multiply(BigDecimal.valueOf(item.getQuantityOrdered())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        private List<PurchaseOrderItem> createPurchaseOrderItems(List<PurchaseOrderItemRequest> itemRequests,
                        PurchaseOrder purchaseOrder) {
                List<PurchaseOrderItem> items = new ArrayList<>();

                for (PurchaseOrderItemRequest itemRequest : itemRequests) {
                        Part part = partRepository.findById(itemRequest.getPartId())
                                        .orElseThrow(() -> new IllegalArgumentException(
                                                        "Part not found with ID: " + itemRequest.getPartId()));

                        BigDecimal totalPrice = itemRequest.getUnitPrice()
                                        .multiply(BigDecimal.valueOf(itemRequest.getQuantityOrdered()));

                        PurchaseOrderItem item = PurchaseOrderItem.builder()
                                        .purchaseOrder(purchaseOrder)
                                        .part(part)
                                        .quantityOrdered(itemRequest.getQuantityOrdered())
                                        .unitPrice(itemRequest.getUnitPrice())
                                        .totalPrice(totalPrice)
                                        .build();

                        items.add(purchaseOrderItemRepository.save(item));
                }

                return items;
        }

        private User getCurrentUser() {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                return userRepository.findByUsername(username)
                                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        }

        // ===== MÉTODOS DE MAPEO =====

        private PurchaseOrderResponse mapToPurchaseOrderResponse(PurchaseOrder purchaseOrder) {
                List<PurchaseOrderItemResponse> itemResponses = purchaseOrder.getItems()
                                .stream()
                                .map(this::mapToPurchaseOrderItemResponse)
                                .toList();

                return PurchaseOrderResponse.builder()
                                .id(purchaseOrder.getId())
                                .supplier(mapToSupplierResponse(purchaseOrder.getSupplier()))
                                .totalAmount(purchaseOrder.getTotalAmount())
                                .orderDate(purchaseOrder.getOrderDate())
                                .expectedDeliveryDate(purchaseOrder.getExpectedDeliveryDate())
                                .actualDeliveryDate(purchaseOrder.getActualDeliveryDate())
                                .status(mapToPurchaseOrderStatusResponse(purchaseOrder.getStatus()))
                                .createdBy(mapToUserResponse(purchaseOrder.getCreatedBy()))
                                .createdAt(purchaseOrder.getCreatedAt())
                                .updatedAt(purchaseOrder.getUpdatedAt())
                                .items(itemResponses)
                                .build();
        }

        private PurchaseOrderItemResponse mapToPurchaseOrderItemResponse(PurchaseOrderItem item) {
                return PurchaseOrderItemResponse.builder()
                                .id(item.getId())
                                .part(mapToPartResponse(item.getPart()))
                                .quantityOrdered(item.getQuantityOrdered())
                                .quantityReceived(item.getQuantityReceived())
                                .unitPrice(item.getUnitPrice())
                                .totalPrice(item.getTotalPrice())
                                .createdAt(item.getCreatedAt())
                                .isFullyReceived(item.getQuantityReceived().equals(item.getQuantityOrdered()))
                                .build();
        }

        private SupplierResponse mapToSupplierResponse(Supplier supplier) {
                PersonResponse personResponse = null;
                if (supplier.getPerson() != null) {
                        personResponse = PersonResponse.builder()
                                        .cui(supplier.getPerson().getCui())
                                        .nit(supplier.getPerson().getNit())
                                        .firstName(supplier.getPerson().getFirstName())
                                        .lastName(supplier.getPerson().getLastName())
                                        .email(supplier.getPerson().getEmail())
                                        .phone(supplier.getPerson().getPhone())
                                        .birthDate(supplier.getPerson().getBirthDate())
                                        .createdAt(supplier.getPerson().getCreatedAt())
                                        .updatedAt(supplier.getPerson().getUpdatedAt())
                                        .build();
                }

                return SupplierResponse.builder()
                                .id(supplier.getId())
                                .person(personResponse)
                                .companyName(supplier.getCompanyName())
                                .contactEmail(supplier.getContactEmail())
                                .contactPhone(supplier.getContactPhone())
                                .isActive(supplier.getIsActive())
                                .createdAt(supplier.getCreatedAt())
                                .updatedAt(supplier.getUpdatedAt())
                                .build();
        }

        private PurchaseOrderStatusResponse mapToPurchaseOrderStatusResponse(PurchaseOrderStatusEntity status) {
                return PurchaseOrderStatusResponse.builder()
                                .id(status.getId())
                                .name(status.getName())
                                .description(status.getDescription())
                                .createdAt(status.getCreatedAt())
                                .build();
        }

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