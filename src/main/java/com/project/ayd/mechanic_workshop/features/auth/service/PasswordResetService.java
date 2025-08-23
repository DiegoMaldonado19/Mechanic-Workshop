package com.project.ayd.mechanic_workshop.features.auth.service;

import com.project.ayd.mechanic_workshop.features.auth.dto.ChangePasswordRequest;
import com.project.ayd.mechanic_workshop.features.auth.dto.PasswordResetRequest;

public interface PasswordResetService {

    void requestPasswordReset(PasswordResetRequest request);

    void changePassword(ChangePasswordRequest request);

    boolean validateResetToken(String token);

    void cleanupExpiredTokens();
}