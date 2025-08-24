package com.project.ayd.mechanic_workshop.features.common.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressDetailResponse {
    private Long id;
    private String address;
    private MunicipalityResponse municipality;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}