package com.project.ayd.mechanic_workshop.features.vehicles.controller;

import com.project.ayd.mechanic_workshop.features.vehicles.dto.UpdateVehicleRequest;
import com.project.ayd.mechanic_workshop.features.vehicles.dto.VehicleHistoryResponse;
import com.project.ayd.mechanic_workshop.features.vehicles.dto.VehicleRequest;
import com.project.ayd.mechanic_workshop.features.vehicles.dto.VehicleResponse;
import com.project.ayd.mechanic_workshop.features.vehicles.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO', 'ESPECIALISTA', 'CLIENTE')")
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO', 'ESPECIALISTA')")
    public ResponseEntity<List<VehicleResponse>> getAllVehicles() {
        List<VehicleResponse> vehicles = vehicleService.getAllVehicles();
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/{vehicleId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO', 'ESPECIALISTA') or " +
            "@vehicleService.getVehicleById(#vehicleId).owner.cui == authentication.principal.username")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable Long vehicleId) {
        VehicleResponse vehicle = vehicleService.getVehicleById(vehicleId);
        return ResponseEntity.ok(vehicle);
    }

    @GetMapping("/license-plate/{licensePlate}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO', 'ESPECIALISTA')")
    public ResponseEntity<VehicleResponse> getVehicleByLicensePlate(@PathVariable String licensePlate) {
        VehicleResponse vehicle = vehicleService.getVehicleByLicensePlate(licensePlate);
        return ResponseEntity.ok(vehicle);
    }

    @GetMapping("/vin/{vin}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO', 'ESPECIALISTA')")
    public ResponseEntity<VehicleResponse> getVehicleByVin(@PathVariable String vin) {
        VehicleResponse vehicle = vehicleService.getVehicleByVin(vin);
        return ResponseEntity.ok(vehicle);
    }

    @GetMapping("/owner/{ownerCui}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or #ownerCui == authentication.principal.username")
    public ResponseEntity<List<VehicleResponse>> getVehiclesByOwner(@PathVariable String ownerCui) {
        List<VehicleResponse> vehicles = vehicleService.getVehiclesByOwner(ownerCui);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/search/brand")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO', 'ESPECIALISTA')")
    public ResponseEntity<List<VehicleResponse>> searchVehiclesByBrand(@RequestParam String brandName) {
        List<VehicleResponse> vehicles = vehicleService.searchVehiclesByBrand(brandName);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/search/model")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO', 'ESPECIALISTA')")
    public ResponseEntity<List<VehicleResponse>> searchVehiclesByModel(@RequestParam String modelName) {
        List<VehicleResponse> vehicles = vehicleService.searchVehiclesByModel(modelName);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/search/license-plate")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO', 'ESPECIALISTA')")
    public ResponseEntity<List<VehicleResponse>> searchVehiclesByLicensePlate(@RequestParam String licensePlate) {
        List<VehicleResponse> vehicles = vehicleService.searchVehiclesByLicensePlate(licensePlate);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/search/color")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO', 'ESPECIALISTA')")
    public ResponseEntity<List<VehicleResponse>> searchVehiclesByColor(@RequestParam String color) {
        List<VehicleResponse> vehicles = vehicleService.searchVehiclesByColor(color);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/year/{year}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO', 'ESPECIALISTA')")
    public ResponseEntity<List<VehicleResponse>> getVehiclesByYear(@PathVariable Integer year) {
        List<VehicleResponse> vehicles = vehicleService.getVehiclesByYear(year);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/brand/{brandId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO', 'ESPECIALISTA')")
    public ResponseEntity<List<VehicleResponse>> getVehiclesByBrand(@PathVariable Long brandId) {
        List<VehicleResponse> vehicles = vehicleService.getVehiclesByBrand(brandId);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/created-between")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO')")
    public ResponseEntity<List<VehicleResponse>> getVehiclesCreatedBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<VehicleResponse> vehicles = vehicleService.getVehiclesCreatedBetween(startDate, endDate);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/{vehicleId}/history")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO', 'ESPECIALISTA') or " +
            "@vehicleService.getVehicleById(#vehicleId).owner.cui == authentication.principal.username")
    public ResponseEntity<VehicleHistoryResponse> getVehicleHistory(@PathVariable Long vehicleId) {
        VehicleHistoryResponse history = vehicleService.getVehicleHistory(vehicleId);
        return ResponseEntity.ok(history);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<VehicleResponse> createVehicle(@Valid @RequestBody VehicleRequest request) {
        VehicleResponse vehicle = vehicleService.createVehicle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicle);
    }

    @PutMapping("/{vehicleId}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<VehicleResponse> updateVehicle(@PathVariable Long vehicleId,
            @Valid @RequestBody UpdateVehicleRequest request) {
        VehicleResponse vehicle = vehicleService.updateVehicle(vehicleId, request);
        return ResponseEntity.ok(vehicle);
    }

    @DeleteMapping("/{vehicleId}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, String>> deleteVehicle(@PathVariable Long vehicleId) {
        vehicleService.deleteVehicle(vehicleId);
        return ResponseEntity.ok(Map.of("message", "Vehicle deleted successfully"));
    }

    @GetMapping("/count/owner/{ownerCui}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or #ownerCui == authentication.principal.username")
    public ResponseEntity<Map<String, Long>> countVehiclesByOwner(@PathVariable String ownerCui) {
        Long count = vehicleService.countVehiclesByOwner(ownerCui);
        return ResponseEntity.ok(Map.of("count", count));
    }
}