package com.project.ayd.mechanic_workshop.features.users.repository;

import com.project.ayd.mechanic_workshop.features.users.entity.SpecializationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpecializationTypeRepository extends JpaRepository<SpecializationType, Long> {

    Optional<SpecializationType> findByName(String name);

    boolean existsByName(String name);
}