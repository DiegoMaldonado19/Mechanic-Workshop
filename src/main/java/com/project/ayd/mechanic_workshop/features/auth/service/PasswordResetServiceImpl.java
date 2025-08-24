package com.project.ayd.mechanic_workshop.features.auth.service;

import com.project.ayd.mechanic_workshop.features.auth.dto.ChangePasswordRequest;
import com.project.ayd.mechanic_workshop.features.auth.dto.PasswordResetRequest;
import com.project.ayd.mechanic_workshop.features.auth.entity.PasswordResetToken;
import com.project.ayd.mechanic_workshop.features.auth.entity.User;
import com.project.ayd.mechanic_workshop.features.auth.repository.PasswordResetTokenRepository;
import com.project.ayd.mechanic_workshop.features.auth.repository.UserRepository;
import com.project.ayd.mechanic_workshop.features.auth.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void requestPasswordReset(PasswordResetRequest request) {
        User user = userRepository.findActiveByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("No account found with this email"));

        // Delete any existing tokens for this user
        tokenRepository.deleteByUser(user);

        // Create new reset token
        String token = SecurityUtils.generateSecureToken();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiresAt(LocalDateTime.now().plusHours(1)); // 1 hour expiration

        tokenRepository.save(resetToken);

        // Send reset email
        sendPasswordResetEmail(user.getPerson().getEmail(), token);

        log.info("Password reset token generated for user: {}", user.getUsername());
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        PasswordResetToken resetToken = tokenRepository.findValidToken(request.getToken(), LocalDateTime.now())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);

        userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        log.info("Password changed successfully for user: {}", user.getUsername());
    }

    @Override
    public boolean validateResetToken(String token) {
        return tokenRepository.findValidToken(token, LocalDateTime.now()).isPresent();
    }

    @Override
    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredAndUsedTokens(LocalDateTime.now());
        log.debug("Expired and used password reset tokens cleaned up");
    }

    private void sendPasswordResetEmail(String email, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Recuperación de contraseña - Taller Mecánico");
            message.setText(String.format(
                    "Ha solicitado restablecer su contraseña.\n\n" +
                            "Para continuar con el proceso, use el siguiente token de recuperación:\n\n" +
                            "%s\n\n" +
                            "Este token expirará en 1 hora.\n" +
                            "Si no solicitó este cambio, ignore este mensaje.",
                    token));

            mailSender.send(message);
            log.info("Password reset email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", email, e);
            throw new RuntimeException("Failed to send password reset email");
        }
    }
}