package com.project.ayd.mechanic_workshop.features.inventory.service;

import com.project.ayd.mechanic_workshop.features.inventory.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface PurchaseOrderService {

    PurchaseOrderResponse createPurchaseOrder(PurchaseOrderRequest request);

    PurchaseOrderResponse getPurchaseOrderById(Long purchaseOrderId);

    Page<PurchaseOrderResponse> getAllPurchaseOrders(Pageable pageable);

    Page<PurchaseOrderResponse> getPurchaseOrdersBySupplier(Long supplierId, Pageable pageable);

    Page<PurchaseOrderResponse> getPurchaseOrdersByStatus(Long statusId, Pageable pageable);

    Page<PurchaseOrderResponse> getPurchaseOrdersByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);

    PurchaseOrderResponse updatePurchaseOrderStatus(Long purchaseOrderId, Long statusId);

    PurchaseOrderResponse receivePurchaseOrderItem(Long purchaseOrderId, Long itemId, Integer quantityReceived);

    PurchaseOrderResponse completePurchaseOrderDelivery(Long purchaseOrderId);

    List<PurchaseOrderItemResponse> getPendingItems();

    Integer getPendingQuantityForPart(Long partId);

    List<PurchaseOrderStatusResponse> getAllPurchaseOrderStatuses();

    Long countPurchaseOrdersByStatus(String statusName);

    Double getTotalPurchaseAmountInPeriod(LocalDate startDate, LocalDate endDate);
}