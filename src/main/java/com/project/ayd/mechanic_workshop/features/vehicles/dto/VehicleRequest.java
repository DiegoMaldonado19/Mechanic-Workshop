package com.project.ayd.mechanic_workshop.features.vehicles.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VehicleRequest {

    @NotBlank(message = "License plate is required")
    @Size(max = 20, message = "License plate cannot exceed 20 characters")
    @Pattern(regexp = "^[A-Z0-9\\-]+$", message = "License plate format is invalid")
    private String licensePlate;

    @NotNull(message = "Model is required")
    private Long modelId;

    @Size(max = 50, message = "Color cannot exceed 50 characters")
    private String color;

    @Size(max = 50, message = "VIN cannot exceed 50 characters")
    private String vin;

    @NotBlank(message = "Owner CUI is required")
    @Size(min = 13, max = 13, message = "Owner CUI must be 13 characters")
    private String ownerCui;
}