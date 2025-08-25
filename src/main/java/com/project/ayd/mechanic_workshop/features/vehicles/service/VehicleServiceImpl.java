package com.project.ayd.mechanic_workshop.features.vehicles.service;

import com.project.ayd.mechanic_workshop.features.auth.entity.Person;
import com.project.ayd.mechanic_workshop.features.auth.repository.PersonRepository;
import com.project.ayd.mechanic_workshop.features.vehicles.dto.UpdateVehicleRequest;
import com.project.ayd.mechanic_workshop.features.vehicles.dto.VehicleHistoryResponse;
import com.project.ayd.mechanic_workshop.features.vehicles.dto.VehicleRequest;
import com.project.ayd.mechanic_workshop.features.vehicles.dto.VehicleResponse;
import com.project.ayd.mechanic_workshop.features.vehicles.entity.Vehicle;
import com.project.ayd.mechanic_workshop.features.vehicles.entity.VehicleModel;
import com.project.ayd.mechanic_workshop.features.vehicles.repository.VehicleModelRepository;
import com.project.ayd.mechanic_workshop.features.vehicles.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleModelRepository vehicleModelRepository;
    private final PersonRepository personRepository;

    @Override
    @Transactional
    public VehicleResponse createVehicle(VehicleRequest request) {
        validateVehicleRequest(request);

        Person owner = personRepository.findById(request.getOwnerCui())
                .orElseThrow(() -> new IllegalArgumentException("Owner not found with CUI: " + request.getOwnerCui()));

        VehicleModel model = vehicleModelRepository.findById(request.getModelId())
                .orElseThrow(
                        () -> new IllegalArgumentException("Vehicle model not found with ID: " + request.getModelId()));

        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate(request.getLicensePlate().toUpperCase());
        vehicle.setModel(model);
        vehicle.setColor(request.getColor());
        vehicle.setVin(request.getVin());
        vehicle.setOwner(owner);

        vehicle = vehicleRepository.save(vehicle);
        log.info("Vehicle created successfully with ID: {}", vehicle.getId());

        return mapToVehicleResponse(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponse getVehicleById(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + vehicleId));
        return mapToVehicleResponse(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponse getVehicleByLicensePlate(String licensePlate) {
        Vehicle vehicle = vehicleRepository.findByLicensePlate(licensePlate.toUpperCase())
                .orElseThrow(
                        () -> new IllegalArgumentException("Vehicle not found with license plate: " + licensePlate));
        return mapToVehicleResponse(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponse getVehicleByVin(String vin) {
        Vehicle vehicle = vehicleRepository.findByVin(vin)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with VIN: " + vin));
        return mapToVehicleResponse(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getAllVehicles() {
        return vehicleRepository.findAll().stream()
                .map(this::mapToVehicleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getVehiclesByOwner(String ownerCui) {
        return vehicleRepository.findByOwnerCuiOrderByCreatedAtDesc(ownerCui).stream()
                .map(this::mapToVehicleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> searchVehiclesByBrand(String brandName) {
        return vehicleRepository.findByBrandNameContaining(brandName).stream()
                .map(this::mapToVehicleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> searchVehiclesByModel(String modelName) {
        return vehicleRepository.findByModelNameContaining(modelName).stream()
                .map(this::mapToVehicleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> searchVehiclesByLicensePlate(String licensePlate) {
        return vehicleRepository.findByLicensePlateContaining(licensePlate.toUpperCase()).stream()
                .map(this::mapToVehicleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> searchVehiclesByColor(String color) {
        return vehicleRepository.findByColorContaining(color).stream()
                .map(this::mapToVehicleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getVehiclesByYear(Integer year) {
        return vehicleRepository.findByModelYear(year).stream()
                .map(this::mapToVehicleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getVehiclesByBrand(Long brandId) {
        return vehicleRepository.findByBrandId(brandId).stream()
                .map(this::mapToVehicleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getVehiclesCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return vehicleRepository.findByCreatedAtBetween(startDate, endDate).stream()
                .map(this::mapToVehicleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VehicleResponse updateVehicle(Long vehicleId, UpdateVehicleRequest request) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + vehicleId));

        if (request.getLicensePlate() != null) {
            String newLicensePlate = request.getLicensePlate().toUpperCase();
            if (!vehicle.getLicensePlate().equals(newLicensePlate) &&
                    vehicleRepository.existsByLicensePlate(newLicensePlate)) {
                throw new IllegalArgumentException("License plate is already in use");
            }
            vehicle.setLicensePlate(newLicensePlate);
        }

        if (request.getModelId() != null) {
            VehicleModel model = vehicleModelRepository.findById(request.getModelId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Vehicle model not found with ID: " + request.getModelId()));
            vehicle.setModel(model);
        }

        if (request.getColor() != null) {
            vehicle.setColor(request.getColor());
        }

        if (request.getVin() != null) {
            if (!vehicle.getVin().equals(request.getVin()) &&
                    vehicleRepository.existsByVin(request.getVin())) {
                throw new IllegalArgumentException("VIN is already in use");
            }
            vehicle.setVin(request.getVin());
        }

        if (request.getOwnerCui() != null) {
            Person owner = personRepository.findById(request.getOwnerCui())
                    .orElseThrow(
                            () -> new IllegalArgumentException("Owner not found with CUI: " + request.getOwnerCui()));
            vehicle.setOwner(owner);
        }

        vehicle = vehicleRepository.save(vehicle);
        log.info("Vehicle updated successfully with ID: {}", vehicleId);

        return mapToVehicleResponse(vehicle);
    }

    @Override
    @Transactional
    public void deleteVehicle(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + vehicleId));

        // Verificar que no tenga trabajos activos (se implementará en el módulo de
        // workorders)
        vehicleRepository.delete(vehicle);
        log.info("Vehicle deleted successfully with ID: {}", vehicleId);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleHistoryResponse getVehicleHistory(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with ID: " + vehicleId));

        // Esta implementación será completada cuando se implemente el módulo de
        // workorders
        return VehicleHistoryResponse.builder()
                .vehicleId(vehicle.getId())
                .licensePlate(vehicle.getLicensePlate())
                .workHistory(List.of())
                .totalSpent(BigDecimal.ZERO)
                .totalWorks(0)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countVehiclesByOwner(String ownerCui) {
        return vehicleRepository.countByOwnerCui(ownerCui);
    }

    private void validateVehicleRequest(VehicleRequest request) {
        if (vehicleRepository.existsByLicensePlate(request.getLicensePlate().toUpperCase())) {
            throw new IllegalArgumentException("License plate is already in use");
        }

        if (request.getVin() != null && vehicleRepository.existsByVin(request.getVin())) {
            throw new IllegalArgumentException("VIN is already in use");
        }
    }

    private VehicleResponse mapToVehicleResponse(Vehicle vehicle) {
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .licensePlate(vehicle.getLicensePlate())
                .color(vehicle.getColor())
                .vin(vehicle.getVin())
                .model(mapToModelResponse(vehicle.getModel()))
                .owner(mapToOwnerResponse(vehicle.getOwner()))
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .build();
    }

    private VehicleResponse.VehicleModelResponse mapToModelResponse(VehicleModel model) {
        return VehicleResponse.VehicleModelResponse.builder()
                .id(model.getId())
                .name(model.getName())
                .year(model.getYear())
                .brand(VehicleResponse.VehicleBrandResponse.builder()
                        .id(model.getBrand().getId())
                        .name(model.getBrand().getName())
                        .country(VehicleResponse.CountryResponse.builder()
                                .id(model.getBrand().getCountry().getId())
                                .name(model.getBrand().getCountry().getName())
                                .build())
                        .build())
                .engineSize(VehicleResponse.EngineSizeResponse.builder()
                        .id(model.getEngineSize().getId())
                        .size(model.getEngineSize().getSize().toString())
                        .description(model.getEngineSize().getDescription())
                        .build())
                .transmissionType(VehicleResponse.TransmissionTypeResponse.builder()
                        .id(model.getTransmissionType().getId())
                        .name(model.getTransmissionType().getName())
                        .description(model.getTransmissionType().getDescription())
                        .build())
                .fuelType(VehicleResponse.FuelTypeResponse.builder()
                        .id(model.getFuelType().getId())
                        .name(model.getFuelType().getName())
                        .description(model.getFuelType().getDescription())
                        .build())
                .build();
    }

    private VehicleResponse.OwnerResponse mapToOwnerResponse(Person owner) {
        return VehicleResponse.OwnerResponse.builder()
                .cui(owner.getCui())
                .nit(owner.getNit())
                .firstName(owner.getFirstName())
                .lastName(owner.getLastName())
                .email(owner.getEmail())
                .phone(owner.getPhone())
                .build();
    }
}