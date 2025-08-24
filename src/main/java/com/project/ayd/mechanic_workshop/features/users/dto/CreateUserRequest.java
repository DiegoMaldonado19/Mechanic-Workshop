package com.project.ayd.mechanic_workshop.features.users.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateUserRequest {

    @NotBlank(message = "CUI is required")
    @Size(min = 13, max = 13, message = "CUI must be 13 characters")
    private String cui;

    @NotBlank(message = "NIT is required")
    @Size(max = 15, message = "NIT cannot exceed 15 characters")
    private String nit;

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name cannot exceed 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    private String lastName;

    @Email(message = "Email must be valid")
    private String email;

    @Pattern(regexp = "^[0-9+\\-\\s()]*$", message = "Phone number format is invalid")
    private String phone;

    private LocalDate birthDate;

    private Long genderId;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotNull(message = "User type is required")
    private Long userTypeId;
}