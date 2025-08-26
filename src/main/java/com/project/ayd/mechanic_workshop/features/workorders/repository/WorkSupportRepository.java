package com.project.ayd.mechanic_workshop.features.workorders.repository;

import com.project.ayd.mechanic_workshop.features.workorders.entity.WorkSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkSupportRepository extends JpaRepository<WorkSupport, Long> {

    @Query("SELECT ws FROM WorkSupport ws LEFT JOIN FETCH ws.work w LEFT JOIN FETCH w.vehicle LEFT JOIN FETCH ws.requestedBy LEFT JOIN FETCH ws.assignedSpecialist LEFT JOIN FETCH ws.specializationNeeded WHERE ws.id = :id")
    Optional<WorkSupport> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT ws FROM WorkSupport ws WHERE ws.work.id = :workId ORDER BY ws.createdAt DESC")
    List<WorkSupport> findByWorkIdOrderByCreatedAtDesc(@Param("workId") Long workId);

    @Query("SELECT ws FROM WorkSupport ws WHERE ws.requestedBy.id = :userId ORDER BY ws.createdAt DESC")
    Page<WorkSupport> findByRequestedByIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT ws FROM WorkSupport ws WHERE ws.assignedSpecialist.id = :specialistId ORDER BY ws.createdAt DESC")
    Page<WorkSupport> findByAssignedSpecialistIdOrderByCreatedAtDesc(@Param("specialistId") Long specialistId,
            Pageable pageable);

    @Query("SELECT ws FROM WorkSupport ws WHERE ws.status = :status ORDER BY ws.urgencyLevel DESC, ws.createdAt ASC")
    Page<WorkSupport> findByStatusOrderByUrgencyAndCreated(@Param("status") WorkSupport.SupportStatus status,
            Pageable pageable);

    @Query("SELECT ws FROM WorkSupport ws WHERE ws.specializationNeeded.id = :specializationId AND ws.status IN ('PENDING', 'ASSIGNED') ORDER BY ws.urgencyLevel DESC, ws.createdAt ASC")
    List<WorkSupport> findPendingSupportBySpecialization(@Param("specializationId") Long specializationId);

    @Query("SELECT ws FROM WorkSupport ws WHERE ws.urgencyLevel = :urgencyLevel ORDER BY ws.createdAt ASC")
    List<WorkSupport> findByUrgencyLevelOrderByCreatedAtAsc(@Param("urgencyLevel") Integer urgencyLevel);

    @Query("SELECT COUNT(ws) FROM WorkSupport ws WHERE ws.status = :status")
    Long countByStatus(@Param("status") WorkSupport.SupportStatus status);

    @Query("SELECT COUNT(ws) FROM WorkSupport ws WHERE ws.assignedSpecialist.id = :specialistId AND ws.status IN ('ASSIGNED', 'IN_PROGRESS')")
    Long countActiveSupportsForSpecialist(@Param("specialistId") Long specialistId);

    @Query("SELECT ws FROM WorkSupport ws WHERE ws.assignedSpecialist IS NULL AND ws.status = 'PENDING' ORDER BY ws.urgencyLevel DESC, ws.createdAt ASC")
    List<WorkSupport> findUnassignedPendingSupports();

    @Query("SELECT ws FROM WorkSupport ws WHERE ws.work.assignedEmployee.id = :employeeId ORDER BY ws.createdAt DESC")
    List<WorkSupport> findSupportRequestsByEmployee(@Param("employeeId") Long employeeId);

    @Query("SELECT DISTINCT ws.specializationNeeded FROM WorkSupport ws WHERE ws.status = 'PENDING'")
    List<WorkSupport> findRequiredSpecializationsForPendingSupports();
}