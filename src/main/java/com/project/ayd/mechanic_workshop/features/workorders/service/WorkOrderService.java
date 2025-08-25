package com.project.ayd.mechanic_workshop.features.workorders.service;

import com.project.ayd.mechanic_workshop.features.workorders.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface WorkOrderService {

    WorkOrderResponse createWorkOrder(WorkOrderRequest request);

    WorkOrderResponse getWorkOrderById(Long workOrderId);

    Page<WorkOrderResponse> getAllWorkOrders(Pageable pageable);

    WorkOrderResponse updateWorkOrder(Long workOrderId, UpdateWorkOrderRequest request);

    void deleteWorkOrder(Long workOrderId);

    WorkOrderResponse assignWorkOrder(Long workOrderId, AssignWorkOrderRequest request);

    WorkOrderResponse startWorkOrder(Long workOrderId);

    WorkOrderResponse completeWorkOrder(Long workOrderId);

    WorkOrderResponse cancelWorkOrder(Long workOrderId);

    WorkOrderResponse approveWorkOrder(Long workOrderId);

    Page<WorkOrderResponse> getWorkOrdersByEmployee(Long employeeId, Pageable pageable);

    Page<WorkOrderResponse> getWorkOrdersByClient(String clientCui, Pageable pageable);

    Page<WorkOrderResponse> getWorkOrdersByStatus(Long statusId, Pageable pageable);

    Page<WorkOrderResponse> getWorkOrdersByServiceType(Long serviceTypeId, Pageable pageable);

    Page<WorkOrderResponse> getWorkOrdersByVehicle(Long vehicleId, Pageable pageable);

    Page<WorkOrderResponse> getWorkOrdersByPriority(Integer priorityLevel, Pageable pageable);

    Page<WorkOrderResponse> getWorkOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    List<WorkOrderResponse> getActiveWorkOrdersByEmployee(Long employeeId);

    Long countWorkOrdersByStatus(Long statusId);

    Long countActiveWorkOrdersByEmployee(Long employeeId);

    WorkProgressResponse addWorkProgress(WorkProgressRequest request);

    List<WorkProgressResponse> getWorkProgressByWorkOrder(Long workOrderId);

    WorkPartResponse addWorkPart(WorkPartRequest request);

    List<WorkPartResponse> getWorkPartsByWorkOrder(Long workOrderId);

    QuotationResponse createQuotation(QuotationRequest request);

    List<QuotationResponse> getQuotationsByWorkOrder(Long workOrderId);

    QuotationResponse approveQuotation(Long quotationId);

    List<ServiceTypeResponse> getAllServiceTypes();

    List<WorkStatusResponse> getAllWorkStatuses();
}