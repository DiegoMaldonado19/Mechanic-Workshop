package com.project.ayd.mechanic_workshop.features.inventory.repository;

import com.project.ayd.mechanic_workshop.features.inventory.entity.ReferenceTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReferenceTypeRepository extends JpaRepository<ReferenceTypeEntity, Long> {
    Optional<ReferenceTypeEntity> findByName(String name);
}