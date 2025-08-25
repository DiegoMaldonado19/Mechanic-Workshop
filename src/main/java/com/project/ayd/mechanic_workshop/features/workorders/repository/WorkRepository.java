package com.project.ayd.mechanic_workshop.features.workorders.repository;

import com.project.ayd.mechanic_workshop.features.workorders.entity.Work;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkRepository extends JpaRepository<Work, Long> {

    @Query("SELECT w FROM Work w LEFT JOIN FETCH w.vehicle v LEFT JOIN FETCH w.serviceType st LEFT JOIN FETCH w.workStatus ws LEFT JOIN FETCH w.assignedEmployee ae LEFT JOIN FETCH w.createdBy cb WHERE w.id = :id")
    Optional<Work> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT w FROM Work w WHERE w.assignedEmployee.id = :employeeId ORDER BY w.createdAt DESC")
    Page<Work> findByAssignedEmployeeId(@Param("employeeId") Long employeeId, Pageable pageable);

    @Query("SELECT w FROM Work w WHERE w.vehicle.ownerCui = :ownerCui ORDER BY w.createdAt DESC")
    Page<Work> findByVehicleOwnerCui(@Param("ownerCui") String ownerCui, Pageable pageable);

    @Query("SELECT w FROM Work w WHERE w.workStatus.id = :statusId ORDER BY w.createdAt DESC")
    Page<Work> findByWorkStatusId(@Param("statusId") Long statusId, Pageable pageable);

    @Query("SELECT w FROM Work w WHERE w.serviceType.id = :serviceTypeId ORDER BY w.createdAt DESC")
    Page<Work> findByServiceTypeId(@Param("serviceTypeId") Long serviceTypeId, Pageable pageable);

    @Query("SELECT w FROM Work w WHERE w.vehicle.id = :vehicleId ORDER BY w.createdAt DESC")
    List<Work> findByVehicleIdOrderByCreatedAtDesc(@Param("vehicleId") Long vehicleId);

    @Query("SELECT w FROM Work w WHERE w.priorityLevel = :priorityLevel ORDER BY w.createdAt DESC")
    Page<Work> findByPriorityLevel(@Param("priorityLevel") Integer priorityLevel, Pageable pageable);

    @Query("SELECT w FROM Work w WHERE w.createdAt BETWEEN :startDate AND :endDate ORDER BY w.createdAt DESC")
    Page<Work> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate, Pageable pageable);

    @Query("SELECT w FROM Work w WHERE w.clientApproved = :approved ORDER BY w.createdAt DESC")
    Page<Work> findByClientApproved(@Param("approved") Boolean approved, Pageable pageable);

    @Query("SELECT COUNT(w) FROM Work w WHERE w.workStatus.id = :statusId")
    Long countByWorkStatusId(@Param("statusId") Long statusId);

    @Query("SELECT COUNT(w) FROM Work w WHERE w.assignedEmployee.id = :employeeId AND w.workStatus.id IN (2, 3)")
    Long countActiveWorksByEmployeeId(@Param("employeeId") Long employeeId);

    @Query("SELECT w FROM Work w WHERE w.assignedEmployee.id = :employeeId AND w.workStatus.id IN (2, 3) ORDER BY w.priorityLevel DESC, w.createdAt ASC")
    List<Work> findActiveWorksByEmployeeId(@Param("employeeId") Long employeeId);
}