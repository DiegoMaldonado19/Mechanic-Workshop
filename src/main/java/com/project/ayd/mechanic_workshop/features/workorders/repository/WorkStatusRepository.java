package com.project.ayd.mechanic_workshop.features.workorders.repository;

import com.project.ayd.mechanic_workshop.features.workorders.entity.WorkStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkStatusRepository extends JpaRepository<WorkStatus, Long> {

    @Query("SELECT ws FROM WorkStatus ws WHERE LOWER(ws.name) = LOWER(:name)")
    Optional<WorkStatus> findByNameIgnoreCase(@Param("name") String name);

    @Query("SELECT CASE WHEN COUNT(ws) > 0 THEN true ELSE false END FROM WorkStatus ws WHERE LOWER(ws.name) = LOWER(:name)")
    Boolean existsByNameIgnoreCase(@Param("name") String name);
}