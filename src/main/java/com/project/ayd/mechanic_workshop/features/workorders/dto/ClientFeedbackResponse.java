package com.project.ayd.mechanic_workshop.features.workorders.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientFeedbackResponse {

    private Long id;
    private Long workId;
    private String clientCui;
    private String clientName;
    private Integer rating;
    private String comments;
    private Boolean wouldRecommend;
    private LocalDateTime createdAt;
    private String vehicleLicensePlate;
    private String serviceTypeName;
}