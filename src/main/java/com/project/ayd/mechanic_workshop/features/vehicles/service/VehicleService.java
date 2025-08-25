package com.project.ayd.mechanic_workshop.features.vehicles.service;

import com.project.ayd.mechanic_workshop.features.vehicles.dto.UpdateVehicleRequest;
import com.project.ayd.mechanic_workshop.features.vehicles.dto.VehicleHistoryResponse;
import com.project.ayd.mechanic_workshop.features.vehicles.dto.VehicleRequest;
import com.project.ayd.mechanic_workshop.features.vehicles.dto.VehicleResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface VehicleService {

    VehicleResponse createVehicle(VehicleRequest request);

    VehicleResponse getVehicleById(Long vehicleId);

    VehicleResponse getVehicleByLicensePlate(String licensePlate);

    VehicleResponse getVehicleByVin(String vin);

    List<VehicleResponse> getAllVehicles();

    List<VehicleResponse> getVehiclesByOwner(String ownerCui);

    List<VehicleResponse> searchVehiclesByBrand(String brandName);

    List<VehicleResponse> searchVehiclesByModel(String modelName);

    List<VehicleResponse> searchVehiclesByLicensePlate(String licensePlate);

    List<VehicleResponse> searchVehiclesByColor(String color);

    List<VehicleResponse> getVehiclesByYear(Integer year);

    List<VehicleResponse> getVehiclesByBrand(Long brandId);

    List<VehicleResponse> getVehiclesCreatedBetween(LocalDateTime startDate, LocalDateTime endDate);

    VehicleResponse updateVehicle(Long vehicleId, UpdateVehicleRequest request);

    void deleteVehicle(Long vehicleId);

    VehicleHistoryResponse getVehicleHistory(Long vehicleId);

    Long countVehiclesByOwner(String ownerCui);
}