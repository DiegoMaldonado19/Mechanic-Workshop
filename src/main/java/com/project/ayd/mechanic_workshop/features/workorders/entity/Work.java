package com.project.ayd.mechanic_workshop.features.workorders.entity;

import com.project.ayd.mechanic_workshop.features.auth.entity.User;
import com.project.ayd.mechanic_workshop.features.vehicles.entity.Vehicle;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "work")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Work {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_type_id", nullable = false)
    private ServiceType serviceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_status_id", nullable = false)
    private WorkStatus workStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_employee_id")
    private User assignedEmployee;

    @Column(name = "problem_description", nullable = false, columnDefinition = "TEXT")
    private String problemDescription;

    @Column(name = "estimated_hours", precision = 5, scale = 2)
    private BigDecimal estimatedHours;

    @Column(name = "actual_hours", precision = 5, scale = 2)
    private BigDecimal actualHours;

    @Column(name = "estimated_cost", precision = 10, scale = 2)
    private BigDecimal estimatedCost;

    @Column(name = "actual_cost", precision = 10, scale = 2)
    private BigDecimal actualCost;

    @Column(name = "client_approved")
    @Builder.Default
    private Boolean clientApproved = false;

    @Column(name = "client_approved_at")
    private LocalDateTime clientApprovedAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "priority_level")
    @Builder.Default
    private Integer priorityLevel = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "work", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WorkProgress> workProgressList;

    @OneToMany(mappedBy = "work", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WorkPart> workParts;

    @OneToMany(mappedBy = "work", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WorkOrderQuotation> quotations;

    @OneToMany(mappedBy = "work", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ClientFeedback> clientFeedbacks;

    public BigDecimal calculateTotalPartsCost() {
        if (workParts == null)
            return BigDecimal.ZERO;
        return workParts.stream()
                .filter(wp -> wp.getQuantityUsed() != null && wp.getUnitPrice() != null)
                .map(wp -> wp.getUnitPrice().multiply(BigDecimal.valueOf(wp.getQuantityUsed())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateTotalLaborHours() {
        if (workProgressList == null)
            return BigDecimal.ZERO;
        return workProgressList.stream()
                .filter(wp -> wp.getHoursWorked() != null)
                .map(WorkProgress::getHoursWorked)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}