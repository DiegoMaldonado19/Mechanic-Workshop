package com.project.ayd.mechanic_workshop.features.workorders.repository;

import com.project.ayd.mechanic_workshop.features.workorders.entity.AdditionalServiceRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdditionalServiceRequestRepository extends JpaRepository<AdditionalServiceRequest, Long> {

        @Query("SELECT asr FROM AdditionalServiceRequest asr LEFT JOIN FETCH asr.work w LEFT JOIN FETCH w.vehicle LEFT JOIN FETCH asr.serviceType LEFT JOIN FETCH asr.requestedBy LEFT JOIN FETCH asr.approvedBy WHERE asr.id = :id")
        Optional<AdditionalServiceRequest> findByIdWithDetails(@Param("id") Long id);

        @Query("SELECT asr FROM AdditionalServiceRequest asr WHERE asr.work.id = :workId ORDER BY asr.createdAt DESC")
        List<AdditionalServiceRequest> findByWorkIdOrderByCreatedAtDesc(@Param("workId") Long workId);

        @Query("SELECT asr FROM AdditionalServiceRequest asr WHERE asr.requestedBy.id = :userId ORDER BY asr.createdAt DESC")
        Page<AdditionalServiceRequest> findByRequestedByIdOrderByCreatedAtDesc(@Param("userId") Long userId,
                        Pageable pageable);

        @Query("SELECT asr FROM AdditionalServiceRequest asr WHERE asr.status = :status ORDER BY asr.urgencyLevel DESC, asr.createdAt ASC")
        Page<AdditionalServiceRequest> findByStatusOrderByUrgencyAndCreated(
                        @Param("status") AdditionalServiceRequest.RequestStatus status, Pageable pageable);

        // CORRECCIÓN: Cambiar ownerCui por owner.cui
        @Query("SELECT asr FROM AdditionalServiceRequest asr WHERE asr.work.vehicle.owner.cui = :clientCui ORDER BY asr.createdAt DESC")
        Page<AdditionalServiceRequest> findByClientCuiOrderByCreatedAtDesc(@Param("clientCui") String clientCui,
                        Pageable pageable);

        @Query("SELECT asr FROM AdditionalServiceRequest asr WHERE asr.clientApproved = :approved ORDER BY asr.createdAt DESC")
        List<AdditionalServiceRequest> findByClientApprovedOrderByCreatedAtDesc(@Param("approved") Boolean approved);

        @Query("SELECT asr FROM AdditionalServiceRequest asr WHERE asr.urgencyLevel = :urgencyLevel ORDER BY asr.createdAt ASC")
        List<AdditionalServiceRequest> findByUrgencyLevelOrderByCreatedAtAsc(
                        @Param("urgencyLevel") Integer urgencyLevel);

        @Query("SELECT COUNT(asr) FROM AdditionalServiceRequest asr WHERE asr.status = :status")
        Long countByStatus(@Param("status") AdditionalServiceRequest.RequestStatus status);

        @Query("SELECT COUNT(asr) FROM AdditionalServiceRequest asr WHERE asr.work.id = :workId")
        Long countByWorkId(@Param("workId") Long workId);

        @Query("SELECT asr FROM AdditionalServiceRequest asr WHERE asr.status = 'PENDING_APPROVAL' AND asr.work.vehicle.owner.cui = :clientCui ORDER BY asr.createdAt ASC")
        List<AdditionalServiceRequest> findPendingApprovalByClient(@Param("clientCui") String clientCui);

        @Query("SELECT asr FROM AdditionalServiceRequest asr WHERE asr.status = 'APPROVED' AND asr.clientApproved = false ORDER BY asr.createdAt ASC")
        List<AdditionalServiceRequest> findApprovedButNotClientApproved();

        @Query("SELECT asr FROM AdditionalServiceRequest asr WHERE asr.work.assignedEmployee.id = :employeeId ORDER BY asr.createdAt DESC")
        List<AdditionalServiceRequest> findRequestsByAssignedEmployee(@Param("employeeId") Long employeeId);
}