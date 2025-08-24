package com.project.ayd.mechanic_workshop.features.users.dto;

import lombok.Data;

@Data
public class UserSearchRequest {
    private String email;
    private String name;
    private String userType;
    private Boolean isActive;
    private String cui;
}