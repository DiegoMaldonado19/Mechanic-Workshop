package com.project.ayd.mechanic_workshop.features.workorders.repository;

import com.project.ayd.mechanic_workshop.features.workorders.entity.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceTypeRepository extends JpaRepository<ServiceType, Long> {

    @Query("SELECT st FROM ServiceType st WHERE LOWER(st.name) = LOWER(:name)")
    Optional<ServiceType> findByNameIgnoreCase(@Param("name") String name);

    @Query("SELECT CASE WHEN COUNT(st) > 0 THEN true ELSE false END FROM ServiceType st WHERE LOWER(st.name) = LOWER(:name)")
    Boolean existsByNameIgnoreCase(@Param("name") String name);
}