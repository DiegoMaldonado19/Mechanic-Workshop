package com.project.ayd.mechanic_workshop.features.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {

    @Size(max = 15, message = "NIT cannot exceed 15 characters")
    private String nit;

    @Size(max = 100, message = "First name cannot exceed 100 characters")
    private String firstName;

    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    private String lastName;

    @Email(message = "Email must be valid")
    private String email;

    @Pattern(regexp = "^[0-9+\\-\\s()]*$", message = "Phone number format is invalid")
    private String phone;

    private LocalDate birthDate;

    private Long genderId;

    private Boolean isActive;
}