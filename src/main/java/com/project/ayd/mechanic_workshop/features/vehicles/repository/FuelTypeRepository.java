package com.project.ayd.mechanic_workshop.features.vehicles.repository;

import com.project.ayd.mechanic_workshop.features.vehicles.entity.FuelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FuelTypeRepository extends JpaRepository<FuelType, Long> {

    Optional<FuelType> findByName(String name);

    boolean existsByName(String name);
}