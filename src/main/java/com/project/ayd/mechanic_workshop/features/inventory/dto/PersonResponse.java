package com.project.ayd.mechanic_workshop.features.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonResponse {

    private String cui;
    private String nit;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}