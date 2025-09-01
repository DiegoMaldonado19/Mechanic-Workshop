package com.project.ayd.mechanic_workshop.features.auth.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String refreshToken;

    @Builder.Default
    private String tokenType = "Bearer";

    private Long expiresIn;
    private String username;
    private String userType;
    private String fullName;
    private Boolean requiresTwoFactor;
    private String message;
    private String userCui;
}