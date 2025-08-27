package com.project.ayd.mechanic_workshop.features.billing.repository;

import com.project.ayd.mechanic_workshop.features.billing.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {

    @Query("SELECT pm FROM PaymentMethod pm WHERE UPPER(pm.name) = UPPER(:name)")
    Optional<PaymentMethod> findByNameIgnoreCase(@Param("name") String name);

    boolean existsByNameIgnoreCase(String name);
}