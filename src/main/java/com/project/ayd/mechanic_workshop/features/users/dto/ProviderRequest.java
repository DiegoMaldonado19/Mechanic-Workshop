package com.project.ayd.mechanic_workshop.features.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProviderRequest {

    // Para proveedor persona
    private String cui;
    private String nit;
    private String firstName;
    private String lastName;

    // Para proveedor empresa o información adicional
    @Size(max = 200, message = "Company name cannot exceed 200 characters")
    private String companyName;

    @Email(message = "Contact email must be valid")
    private String contactEmail;

    @Pattern(regexp = "^[0-9+\\-\\s()]*$", message = "Phone number format is invalid")
    private String contactPhone;

    private Long addressDetailId;

    private Boolean isActive = true;
}