package com.project.ayd.mechanic_workshop.features.users.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String cui;
    private String nit;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String username;
    private String userType;
    private String gender;
    private Boolean isActive;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
}