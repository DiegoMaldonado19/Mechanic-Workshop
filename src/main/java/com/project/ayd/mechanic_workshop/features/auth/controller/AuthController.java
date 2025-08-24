package com.project.ayd.mechanic_workshop.features.auth.controller;

import com.project.ayd.mechanic_workshop.features.auth.dto.*;
import com.project.ayd.mechanic_workshop.features.auth.service.AuthService;
import com.project.ayd.mechanic_workshop.features.auth.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-2fa")
    public ResponseEntity<AuthResponse> verifyTwoFactor(@Valid @RequestBody TwoFactorAuthRequest request) {
        AuthResponse response = authService.verifyTwoFactor(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");
        authService.logout(jwtToken);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        AuthResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-2fa")
    public ResponseEntity<Map<String, String>> resendTwoFactorCode(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        authService.sendTwoFactorCode(username);
        return ResponseEntity.ok(Map.of("message", "Two-factor code sent successfully"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody PasswordResetRequest request) {
        passwordResetService.requestPasswordReset(request);
        return ResponseEntity.ok(Map.of("message", "Password reset instructions sent to your email"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ChangePasswordRequest request) {
        passwordResetService.changePassword(request);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    @GetMapping("/validate-reset-token")
    public ResponseEntity<Map<String, Boolean>> validateResetToken(@RequestParam String token) {
        boolean isValid = passwordResetService.validateResetToken(token);
        return ResponseEntity.ok(Map.of("valid", isValid));
    }
}