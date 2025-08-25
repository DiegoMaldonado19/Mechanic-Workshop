package com.project.ayd.mechanic_workshop.features.workorders.repository;

import com.project.ayd.mechanic_workshop.features.workorders.entity.PartCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PartCategoryRepository extends JpaRepository<PartCategory, Long> {

    @Query("SELECT pc FROM PartCategory pc WHERE LOWER(pc.name) = LOWER(:name)")
    Optional<PartCategory> findByNameIgnoreCase(@Param("name") String name);

    @Query("SELECT CASE WHEN COUNT(pc) > 0 THEN true ELSE false END FROM PartCategory pc WHERE LOWER(pc.name) = LOWER(:name)")
    Boolean existsByNameIgnoreCase(@Param("name") String name);
}