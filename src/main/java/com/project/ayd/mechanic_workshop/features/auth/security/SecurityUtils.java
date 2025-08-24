package com.project.ayd.mechanic_workshop.features.auth.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.SecureRandom;
import java.util.Optional;

public class SecurityUtils {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String DIGITS = "0123456789";

    public static Optional<String> getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return Optional.empty();
        }

        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            return Optional.of(userDetails.getUsername());
        }

        if (authentication.getPrincipal() instanceof String) {
            return Optional.of((String) authentication.getPrincipal());
        }

        return Optional.empty();
    }

    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
                authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal());
    }

    public static String generateTwoFactorCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(DIGITS.charAt(SECURE_RANDOM.nextInt(DIGITS.length())));
        }
        return code.toString();
    }

    public static String generateSecureToken() {
        byte[] tokenBytes = new byte[32];
        SECURE_RANDOM.nextBytes(tokenBytes);

        StringBuilder token = new StringBuilder();
        for (byte b : tokenBytes) {
            token.append(String.format("%02x", b));
        }
        return token.toString();
    }
}