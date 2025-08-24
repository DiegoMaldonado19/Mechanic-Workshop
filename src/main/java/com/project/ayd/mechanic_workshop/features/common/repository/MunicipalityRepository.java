package com.project.ayd.mechanic_workshop.features.common.repository;

import com.project.ayd.mechanic_workshop.features.common.entity.Municipality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MunicipalityRepository extends JpaRepository<Municipality, Long> {

    Optional<Municipality> findByName(String name);

    List<Municipality> findByDepartmentId(Long departmentId);

    @Query("SELECT m FROM Municipality m WHERE m.department.id = :departmentId ORDER BY m.name")
    List<Municipality> findByDepartmentIdOrderByName(@Param("departmentId") Long departmentId);

    boolean existsByName(String name);
}