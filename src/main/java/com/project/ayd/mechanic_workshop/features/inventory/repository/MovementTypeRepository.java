package com.project.ayd.mechanic_workshop.features.inventory.repository;

import com.project.ayd.mechanic_workshop.features.inventory.entity.MovementTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovementTypeRepository extends JpaRepository<MovementTypeEntity, Long> {
    Optional<MovementTypeEntity> findByName(String name);
}