package com.project.ayd.mechanic_workshop.features.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProviderUpdateRequest {

    @Size(max = 15, message = "NIT cannot exceed 15 characters")
    private String nit;

    @Size(max = 100, message = "First name cannot exceed 100 characters")
    private String firstName;

    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    private String lastName;

    @Size(max = 200, message = "Company name cannot exceed 200 characters")
    private String companyName;

    @Email(message = "Contact email must be valid")
    private String contactEmail;

    @Pattern(regexp = "^[0-9+\\-\\s()]*$", message = "Phone number format is invalid")
    private String contactPhone;

    private Long addressDetailId;
}