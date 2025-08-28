package com.project.ayd.mechanic_workshop.features.inventory.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierRequest {

    private String personCui;

    @Size(max = 200, message = "Company name must not exceed 200 characters")
    private String companyName;

    @Email(message = "Invalid email format")
    private String contactEmail;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String contactPhone;

    private Long addressDetailId;
}