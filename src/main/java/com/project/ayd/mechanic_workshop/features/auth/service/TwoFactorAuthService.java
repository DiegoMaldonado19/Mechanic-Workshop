package com.project.ayd.mechanic_workshop.features.auth.service;

public interface TwoFactorAuthService {

    void sendTwoFactorCode(String email, String code);

    void sendTwoFactorCodeSms(String phoneNumber, String code);
}