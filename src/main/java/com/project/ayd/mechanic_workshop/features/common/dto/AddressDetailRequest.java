package com.project.ayd.mechanic_workshop.features.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddressDetailRequest {

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Municipality is required")
    private Long municipalityId;
}