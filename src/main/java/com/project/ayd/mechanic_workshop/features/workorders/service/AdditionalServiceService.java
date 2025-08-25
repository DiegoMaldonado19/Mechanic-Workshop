package com.project.ayd.mechanic_workshop.features.workorders.service;

import com.project.ayd.mechanic_workshop.features.workorders.dto.AdditionalServiceRequestDto;
import com.project.ayd.mechanic_workshop.features.workorders.dto.AdditionalServiceResponse;
import com.project.ayd.mechanic_workshop.features.workorders.dto.ApproveAdditionalServiceRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface AdditionalServiceService {

    AdditionalServiceResponse requestAdditionalService(AdditionalServiceRequestDto request);

    AdditionalServiceResponse getAdditionalServiceById(Long requestId);

    List<AdditionalServiceResponse> getAdditionalServicesByWork(Long workId);

    Page<AdditionalServiceResponse> getAdditionalServicesByEmployee(Long employeeId, Pageable pageable);

    Page<AdditionalServiceResponse> getAdditionalServicesByClient(String clientCui, Pageable pageable);

    Page<AdditionalServiceResponse> getAdditionalServicesByStatus(String status, Pageable pageable);

    List<AdditionalServiceResponse> getPendingClientApproval(String clientCui);

    AdditionalServiceResponse approveOrRejectService(Long requestId, ApproveAdditionalServiceRequest request);

    AdditionalServiceResponse clientApproveService(Long requestId);

    AdditionalServiceResponse clientRejectService(Long requestId, String reason);

    AdditionalServiceResponse startAdditionalService(Long requestId);

    AdditionalServiceResponse completeAdditionalService(Long requestId);

    List<AdditionalServiceResponse> getApprovedButNotClientApproved();

    Map<String, Long> getAdditionalServiceStatistics();

    Long getAdditionalServiceCountByWork(Long workId);

    void deleteAdditionalServiceRequest(Long requestId);
}