package com.project.ayd.mechanic_workshop.features.billing.repository;

import com.project.ayd.mechanic_workshop.features.billing.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentStatusRepository extends JpaRepository<PaymentStatus, Long> {

    @Query("SELECT ps FROM PaymentStatus ps WHERE UPPER(ps.name) = UPPER(:name)")
    Optional<PaymentStatus> findByNameIgnoreCase(@Param("name") String name);

    boolean existsByNameIgnoreCase(String name);
}