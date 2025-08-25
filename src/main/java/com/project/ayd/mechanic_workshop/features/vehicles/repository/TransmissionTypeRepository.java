package com.project.ayd.mechanic_workshop.features.vehicles.repository;

import com.project.ayd.mechanic_workshop.features.vehicles.entity.TransmissionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransmissionTypeRepository extends JpaRepository<TransmissionType, Long> {

    Optional<TransmissionType> findByName(String name);

    boolean existsByName(String name);
}