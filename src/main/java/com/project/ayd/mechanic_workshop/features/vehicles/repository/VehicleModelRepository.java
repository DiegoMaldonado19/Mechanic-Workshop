package com.project.ayd.mechanic_workshop.features.vehicles.repository;

import com.project.ayd.mechanic_workshop.features.vehicles.entity.VehicleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleModelRepository extends JpaRepository<VehicleModel, Long> {

    Optional<VehicleModel> findByName(String name);

    List<VehicleModel> findByBrandId(Long brandId);

    @Query("SELECT vm FROM VehicleModel vm WHERE vm.brand.id = :brandId ORDER BY vm.name")
    List<VehicleModel> findByBrandIdOrderByName(@Param("brandId") Long brandId);

    @Query("SELECT vm FROM VehicleModel vm WHERE vm.year = :year")
    List<VehicleModel> findByYear(@Param("year") Integer year);

    @Query("SELECT vm FROM VehicleModel vm WHERE vm.name LIKE %:name%")
    List<VehicleModel> findByNameContaining(@Param("name") String name);

    @Query("SELECT vm FROM VehicleModel vm WHERE vm.brand.name LIKE %:brandName%")
    List<VehicleModel> findByBrandNameContaining(@Param("brandName") String brandName);

    boolean existsByName(String name);
}