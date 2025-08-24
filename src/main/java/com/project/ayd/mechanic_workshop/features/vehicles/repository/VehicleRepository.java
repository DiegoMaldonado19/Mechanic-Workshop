package com.project.ayd.mechanic_workshop.features.vehicles.repository;

import com.project.ayd.mechanic_workshop.features.vehicles.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByLicensePlate(String licensePlate);

    Optional<Vehicle> findByVin(String vin);

    List<Vehicle> findByOwnerCui(String ownerCui);

    @Query("SELECT v FROM Vehicle v WHERE v.owner.cui = :ownerCui ORDER BY v.createdAt DESC")
    List<Vehicle> findByOwnerCuiOrderByCreatedAtDesc(@Param("ownerCui") String ownerCui);

    @Query("SELECT v FROM Vehicle v WHERE v.model.brand.name LIKE %:brandName%")
    List<Vehicle> findByBrandNameContaining(@Param("brandName") String brandName);

    @Query("SELECT v FROM Vehicle v WHERE v.model.name LIKE %:modelName%")
    List<Vehicle> findByModelNameContaining(@Param("modelName") String modelName);

    @Query("SELECT v FROM Vehicle v WHERE v.licensePlate LIKE %:licensePlate%")
    List<Vehicle> findByLicensePlateContaining(@Param("licensePlate") String licensePlate);

    @Query("SELECT v FROM Vehicle v WHERE v.color LIKE %:color%")
    List<Vehicle> findByColorContaining(@Param("color") String color);

    @Query("SELECT v FROM Vehicle v WHERE v.createdAt BETWEEN :startDate AND :endDate")
    List<Vehicle> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT v FROM Vehicle v WHERE v.model.year = :year")
    List<Vehicle> findByModelYear(@Param("year") Integer year);

    @Query("SELECT v FROM Vehicle v WHERE v.model.brand.id = :brandId")
    List<Vehicle> findByBrandId(@Param("brandId") Long brandId);

    boolean existsByLicensePlate(String licensePlate);

    boolean existsByVin(String vin);

    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.owner.cui = :ownerCui")
    Long countByOwnerCui(@Param("ownerCui") String ownerCui);
}