package com.project.ayd.mechanic_workshop.features.workorders.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkCommentResponseRequest {

    @NotBlank(message = "Response is required")
    @Size(min = 5, max = 1000, message = "Response must be between 5 and 1000 characters")
    private String response;
}