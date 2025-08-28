package com.project.ayd.mechanic_workshop.features.inventory.dto;

import com.project.ayd.mechanic_workshop.features.users.dto.UserResponse;
import com.project.ayd.mechanic_workshop.features.workorders.dto.PartResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementResponse {

    private Long id;
    private PartResponse part;
    private MovementTypeResponse movementType;
    private Integer quantity;
    private ReferenceTypeResponse referenceType;
    private Long referenceId;
    private String notes;
    private UserResponse createdBy;
    private LocalDateTime createdAt;
}