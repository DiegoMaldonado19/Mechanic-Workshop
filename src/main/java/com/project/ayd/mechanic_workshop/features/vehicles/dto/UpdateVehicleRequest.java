package com.project.ayd.mechanic_workshop.features.vehicles.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateVehicleRequest {

    @Size(max = 20, message = "License plate cannot exceed 20 characters")
    @Pattern(regexp = "^[A-Z0-9\\-]+$", message = "License plate format is invalid")
    private String licensePlate;

    private Long modelId;

    @Size(max = 50, message = "Color cannot exceed 50 characters")
    private String color;

    @Size(max = 50, message = "VIN cannot exceed 50 characters")
    private String vin;

    @Size(min = 13, max = 13, message = "Owner CUI must be 13 characters")
    private String ownerCui;
}