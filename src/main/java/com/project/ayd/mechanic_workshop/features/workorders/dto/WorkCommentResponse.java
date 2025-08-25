package com.project.ayd.mechanic_workshop.features.workorders.dto;

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
public class WorkCommentResponse {

    private Long id;
    private Long workId;
    private String workDescription;
    private String vehicleLicensePlate;
    private UserResponse user;
    private String comment;
    private String commentType;
    private Boolean isUrgent;
    private Boolean requiresResponse;
    private String response;
    private UserResponse respondedBy;
    private LocalDateTime respondedAt;
    private LocalDateTime createdAt;
}