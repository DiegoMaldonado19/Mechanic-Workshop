package com.project.ayd.mechanic_workshop.features.workorders.entity;

import com.project.ayd.mechanic_workshop.features.auth.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_progress")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_id", nullable = false)
    private Work work;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "progress_description", nullable = false, columnDefinition = "TEXT")
    private String progressDescription;

    @Column(name = "hours_worked", precision = 5, scale = 2)
    private BigDecimal hoursWorked;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @Column(name = "symptoms_detected", columnDefinition = "TEXT")
    private String symptomsDetected;

    @Column(name = "additional_damage_found", columnDefinition = "TEXT")
    private String additionalDamageFound;

    @CreationTimestamp
    @Column(name = "recorded_at", nullable = false, updatable = false)
    private LocalDateTime recordedAt;
}