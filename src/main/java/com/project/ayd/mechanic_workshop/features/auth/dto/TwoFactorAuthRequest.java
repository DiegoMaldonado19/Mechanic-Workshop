package com.project.ayd.mechanic_workshop.features.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TwoFactorAuthRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Two-factor code is required")
    @Size(min = 6, max = 6, message = "Two-factor code must be 6 digits")
    @Pattern(regexp = "\\d{6}", message = "Two-factor code must contain only digits")
    private String code;
}