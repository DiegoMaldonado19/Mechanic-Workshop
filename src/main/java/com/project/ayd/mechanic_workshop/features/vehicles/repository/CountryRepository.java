package com.project.ayd.mechanic_workshop.features.vehicles.repository;

import com.project.ayd.mechanic_workshop.features.vehicles.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    Optional<Country> findByName(String name);

    boolean existsByName(String name);
}