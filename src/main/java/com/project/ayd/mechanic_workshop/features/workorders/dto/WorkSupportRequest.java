package com.project.ayd.mechanic_workshop.features.workorders.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkSupportRequest {

    @NotNull(message = "Work ID is required")
    @Positive(message = "Work ID must be positive")
    private Long workId;

    @Positive(message = "Specialization needed ID must be positive")
    private Long specializationNeededId;

    @NotBlank(message = "Reason is required")
    @Size(min = 10, max = 1000, message = "Reason must be between 10 and 1000 characters")
    private String reason;

    @Min(value = 1, message = "Urgency level must be at least 1")
    @Max(value = 5, message = "Urgency level cannot exceed 5")
    @Builder.Default
    private Integer urgencyLevel = 1;

    @Positive(message = "Specialist ID must be positive")
    private Long assignedSpecialistId;
}