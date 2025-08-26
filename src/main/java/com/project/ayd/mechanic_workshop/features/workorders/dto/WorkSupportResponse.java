package com.project.ayd.mechanic_workshop.features.workorders.dto;

import com.project.ayd.mechanic_workshop.features.users.dto.SpecializationTypeResponse;
import com.project.ayd.mechanic_workshop.features.users.dto.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkSupportResponse {

    private Long id;
    private Long workId;
    private String workDescription;
    private String vehicleLicensePlate;
    private UserResponse requestedBy;
    private UserResponse assignedSpecialist;
    private SpecializationTypeResponse specializationNeeded;
    private String reason;
    private Integer urgencyLevel;
    private String urgencyDisplayName;
    private String status;
    private String specialistNotes;
    private String resolutionNotes;
    private LocalDateTime assignedAt;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}