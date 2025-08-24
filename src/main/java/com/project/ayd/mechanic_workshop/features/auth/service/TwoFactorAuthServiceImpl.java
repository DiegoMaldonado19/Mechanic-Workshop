package com.project.ayd.mechanic_workshop.features.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TwoFactorAuthServiceImpl implements TwoFactorAuthService {

    private final JavaMailSender mailSender;

    @Override
    public void sendTwoFactorCode(String email, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Código de verificación - Taller Mecánico");
            message.setText(String.format(
                    "Su código de verificación es: %s\n\n" +
                            "Este código expirará en 5 minutos.\n" +
                            "Si no solicitó este código, ignore este mensaje.",
                    code));

            mailSender.send(message);
            log.info("Two-factor code sent to email: {}", email);
        } catch (Exception e) {
            log.error("Failed to send two-factor code to email: {}", email, e);
            throw new RuntimeException("Failed to send verification code");
        }
    }

    @Override
    public void sendTwoFactorCodeSms(String phoneNumber, String code) {
        // SMS implementation would go here
        // For now, we'll just log it
        log.info("Two-factor code sent to SMS: {} - Code: {}", phoneNumber, code);
    }
}