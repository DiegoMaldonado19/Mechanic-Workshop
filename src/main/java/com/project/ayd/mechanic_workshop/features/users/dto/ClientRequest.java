package com.project.ayd.mechanic_workshop.features.users.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ClientRequest extends CreateUserRequest {
    // Los clientes solo necesitan la información base de CreateUserRequest
}