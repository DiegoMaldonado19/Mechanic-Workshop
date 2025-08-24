package com.project.ayd.mechanic_workshop.features.users.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpecializationTypeResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
}