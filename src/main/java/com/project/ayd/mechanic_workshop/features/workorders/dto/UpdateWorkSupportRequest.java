package com.project.ayd.mechanic_workshop.features.workorders.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWorkSupportRequest {

    @Positive(message = "Assigned specialist ID must be positive")
    private Long assignedSpecialistId;

    @Size(max = 1000, message = "Specialist notes cannot exceed 1000 characters")
    private String specialistNotes;

    @Size(max = 1000, message = "Resolution notes cannot exceed 1000 characters")
    private String resolutionNotes;

    private String status;
}