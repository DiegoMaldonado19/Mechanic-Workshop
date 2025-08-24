package com.project.ayd.mechanic_workshop.features.vehicles.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehicleResponse {
    private Long id;
    private String licensePlate;
    private String color;
    private String vin;
    private VehicleModelResponse model;
    private OwnerResponse owner;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VehicleModelResponse {
        private Long id;
        private String name;
        private Integer year;
        private VehicleBrandResponse brand;
        private EngineSizeResponse engineSize;
        private TransmissionTypeResponse transmissionType;
        private FuelTypeResponse fuelType;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VehicleBrandResponse {
        private Long id;
        private String name;
        private CountryResponse country;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CountryResponse {
        private Long id;
        private String name;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EngineSizeResponse {
        private Long id;
        private String size;
        private String description;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TransmissionTypeResponse {
        private Long id;
        private String name;
        private String description;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FuelTypeResponse {
        private Long id;
        private String name;
        private String description;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OwnerResponse {
        private String cui;
        private String nit;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
    }
}