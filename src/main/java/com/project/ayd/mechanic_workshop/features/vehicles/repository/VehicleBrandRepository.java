package com.project.ayd.mechanic_workshop.features.vehicles.repository;

import com.project.ayd.mechanic_workshop.features.vehicles.entity.VehicleBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleBrandRepository extends JpaRepository<VehicleBrand, Long> {

    Optional<VehicleBrand> findByName(String name);

    @Query("SELECT vb FROM VehicleBrand vb WHERE vb.country.id = :countryId ORDER BY vb.name")
    List<VehicleBrand> findByCountryIdOrderByName(@Param("countryId") Long countryId);

    @Query("SELECT vb FROM VehicleBrand vb WHERE vb.name LIKE %:name%")
    List<VehicleBrand> findByNameContaining(@Param("name") String name);

    boolean existsByName(String name);
}