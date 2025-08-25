package com.project.ayd.mechanic_workshop.features.workorders.entity;

import com.project.ayd.mechanic_workshop.features.auth.entity.Person;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "client_feedback", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "work_id", "client_cui" })
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_id", nullable = false)
    private Work work;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_cui", nullable = false, referencedColumnName = "cui")
    private Person client;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Column(name = "would_recommend")
    private Boolean wouldRecommend;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}