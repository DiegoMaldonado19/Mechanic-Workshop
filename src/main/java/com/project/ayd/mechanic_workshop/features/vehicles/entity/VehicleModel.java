package com.project.ayd.mechanic_workshop.features.vehicles.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_model")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class VehicleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "brand_id", nullable = false)
    private VehicleBrand brand;

    @Column(name = "year")
    private Integer year;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "engine_size_id", nullable = false)
    private EngineSize engineSize;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "transmission_type_id", nullable = false)
    private TransmissionType transmissionType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fuel_type_id", nullable = false)
    private FuelType fuelType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}