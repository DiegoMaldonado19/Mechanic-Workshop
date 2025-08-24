package com.project.ayd.mechanic_workshop.features.users.repository;

import com.project.ayd.mechanic_workshop.features.users.entity.EmployeeSpecialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeSpecializationRepository extends JpaRepository<EmployeeSpecialization, Long> {

    List<EmployeeSpecialization> findByUserIdAndIsActiveTrue(Long userId);

    List<EmployeeSpecialization> findByUserId(Long userId);

    @Query("SELECT es FROM EmployeeSpecialization es WHERE es.specializationType.id = :specializationTypeId AND es.isActive = true")
    List<EmployeeSpecialization> findBySpecializationTypeIdAndIsActiveTrue(
            @Param("specializationTypeId") Long specializationTypeId);

    boolean existsByUserIdAndSpecializationTypeIdAndIsActiveTrue(Long userId, Long specializationTypeId);
}