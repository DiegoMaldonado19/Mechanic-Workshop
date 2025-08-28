package com.project.ayd.mechanic_workshop.features.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusResponse {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
}