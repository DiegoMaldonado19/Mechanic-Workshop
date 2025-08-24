package com.project.ayd.mechanic_workshop.features.users.service;

import com.project.ayd.mechanic_workshop.features.users.dto.ProviderRequest;
import com.project.ayd.mechanic_workshop.features.users.dto.UserResponse;

import java.util.List;

public interface ProviderService {

    UserResponse createProvider(ProviderRequest request);

    List<UserResponse> getAllProviders();

    List<UserResponse> getAllActiveProviders();

    UserResponse getProviderById(Long providerId);

    UserResponse getProviderByCui(String cui);

    UserResponse getProviderByEmail(String email);

    List<UserResponse> searchProvidersByCompanyName(String companyName);

    void activateProvider(Long providerId);

    void deactivateProvider(Long providerId);
}