package com.project.ayd.mechanic_workshop.features.auth.service;

import com.project.ayd.mechanic_workshop.features.auth.dto.*;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse verifyTwoFactor(TwoFactorAuthRequest request);

    AuthResponse register(RegisterRequest request);

    void logout(String token);

    AuthResponse refreshToken(String refreshToken);

    void sendTwoFactorCode(String username);

    boolean validateTwoFactorCode(String username, String code);
}