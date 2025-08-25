package com.project.ayd.mechanic_workshop.features.vehicles.repository;

import com.project.ayd.mechanic_workshop.features.vehicles.entity.EngineSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface EngineSizeRepository extends JpaRepository<EngineSize, Long> {

    Optional<EngineSize> findBySize(BigDecimal size);

    boolean existsBySize(BigDecimal size);
}