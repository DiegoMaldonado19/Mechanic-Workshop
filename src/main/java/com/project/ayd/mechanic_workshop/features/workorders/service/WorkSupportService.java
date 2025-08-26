package com.project.ayd.mechanic_workshop.features.workorders.service;

import com.project.ayd.mechanic_workshop.features.workorders.dto.UpdateWorkSupportRequest;
import com.project.ayd.mechanic_workshop.features.workorders.dto.WorkSupportRequest;
import com.project.ayd.mechanic_workshop.features.workorders.dto.WorkSupportResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface WorkSupportService {

    WorkSupportResponse requestSupport(WorkSupportRequest request);

    WorkSupportResponse getSupportById(Long supportId);

    List<WorkSupportResponse> getSupportByWorkId(Long workId);

    Page<WorkSupportResponse> getSupportRequestsByEmployee(Long employeeId, Pageable pageable);

    Page<WorkSupportResponse> getSupportRequestsForSpecialist(Long specialistId, Pageable pageable);

    WorkSupportResponse assignSpecialist(Long supportId, Long specialistId);

    WorkSupportResponse updateSupport(Long supportId, UpdateWorkSupportRequest request);

    WorkSupportResponse startSupport(Long supportId);

    WorkSupportResponse completeSupport(Long supportId);

    WorkSupportResponse cancelSupport(Long supportId);

    Page<WorkSupportResponse> getSupportByStatus(String status, Pageable pageable);

    List<WorkSupportResponse> getPendingSupportBySpecialization(Long specializationId);

    List<WorkSupportResponse> getUnassignedPendingSupports();

    Map<String, Long> getSupportStatistics();

    Long getActiveSupportCountForSpecialist(Long specialistId);

    WorkSupportResponse findBestSpecialistForSupport(Long supportId);

    void deleteSupport(Long supportId);
}