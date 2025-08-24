package com.project.ayd.mechanic_workshop.features.users.service;

import com.project.ayd.mechanic_workshop.features.users.dto.ClientRequest;
import com.project.ayd.mechanic_workshop.features.users.dto.UserResponse;

import java.util.List;

public interface ClientService {

    UserResponse createClient(ClientRequest request);

    List<UserResponse> getAllClients();

    List<UserResponse> getAllActiveClients();

    UserResponse getClientById(Long clientId);

    UserResponse getClientByCui(String cui);

    UserResponse getClientByEmail(String email);
}