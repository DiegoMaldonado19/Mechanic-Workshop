package com.project.ayd.mechanic_workshop.features.users.controller;

import com.project.ayd.mechanic_workshop.features.users.dto.ClientRequest;
import com.project.ayd.mechanic_workshop.features.users.dto.UserResponse;
import com.project.ayd.mechanic_workshop.features.users.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'CLIENTE')")
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<UserResponse>> getAllClients() {
        List<UserResponse> clients = clientService.getAllClients();
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<UserResponse>> getAllActiveClients() {
        List<UserResponse> clients = clientService.getAllActiveClients();
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{clientId}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or @clientService.getClientById(#clientId).username == authentication.name")
    public ResponseEntity<UserResponse> getClientById(@PathVariable Long clientId) {
        UserResponse client = clientService.getClientById(clientId);
        return ResponseEntity.ok(client);
    }

    @GetMapping("/cui/{cui}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UserResponse> getClientByCui(@PathVariable String cui) {
        UserResponse client = clientService.getClientByCui(cui);
        return ResponseEntity.ok(client);
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or #email == authentication.name")
    public ResponseEntity<UserResponse> getClientByEmail(@PathVariable String email) {
        UserResponse client = clientService.getClientByEmail(email);
        return ResponseEntity.ok(client);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UserResponse> createClient(@Valid @RequestBody ClientRequest request) {
        UserResponse client = clientService.createClient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(client);
    }
}