package com.project.ayd.mechanic_workshop.features.inventory.repository;

import com.project.ayd.mechanic_workshop.features.inventory.entity.ReferenceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReferenceTypeRepository extends JpaRepository<ReferenceType, Long> {
    Optional<ReferenceType> findByName(String name);
}