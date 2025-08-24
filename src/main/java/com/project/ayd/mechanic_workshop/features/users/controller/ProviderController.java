package com.project.ayd.mechanic_workshop.features.users.controller;

import com.project.ayd.mechanic_workshop.features.users.dto.ProviderRequest;
import com.project.ayd.mechanic_workshop.features.users.dto.ProviderUpdateRequest;
import com.project.ayd.mechanic_workshop.features.users.dto.UserResponse;
import com.project.ayd.mechanic_workshop.features.users.service.ProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/providers")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROVEEDOR')")
public class ProviderController {

    private final ProviderService providerService;

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<UserResponse>> getAllProviders() {
        List<UserResponse> providers = providerService.getAllProviders();
        return ResponseEntity.ok(providers);
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<UserResponse>> getAllActiveProviders() {
        List<UserResponse> providers = providerService.getAllActiveProviders();
        return ResponseEntity.ok(providers);
    }

    @GetMapping("/{providerId}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UserResponse> getProviderById(@PathVariable Long providerId) {
        UserResponse provider = providerService.getProviderById(providerId);
        return ResponseEntity.ok(provider);
    }

    @GetMapping("/cui/{cui}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UserResponse> getProviderByCui(@PathVariable String cui) {
        UserResponse provider = providerService.getProviderByCui(cui);
        return ResponseEntity.ok(provider);
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UserResponse> getProviderByEmail(@PathVariable String email) {
        UserResponse provider = providerService.getProviderByEmail(email);
        return ResponseEntity.ok(provider);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<UserResponse>> searchProvidersByCompanyName(@RequestParam String companyName) {
        List<UserResponse> providers = providerService.searchProvidersByCompanyName(companyName);
        return ResponseEntity.ok(providers);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UserResponse> createProvider(@Valid @RequestBody ProviderRequest request) {
        UserResponse provider = providerService.createProvider(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(provider);
    }

    @PutMapping("/{providerId}/activate")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, String>> activateProvider(@PathVariable Long providerId) {
        providerService.activateProvider(providerId);
        return ResponseEntity.ok(Map.of("message", "Provider activated successfully"));
    }

    @PutMapping("/{providerId}/deactivate")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, String>> deactivateProvider(@PathVariable Long providerId) {
        providerService.deactivateProvider(providerId);
        return ResponseEntity.ok(Map.of("message", "Provider deactivated successfully"));
    }

    @PutMapping("/{providerId}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UserResponse> updateProvider(@PathVariable Long providerId,
            @Valid @RequestBody ProviderUpdateRequest request) {
        UserResponse provider = providerService.updateProvider(providerId, request);
        return ResponseEntity.ok(provider);
    }
}