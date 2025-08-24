package com.project.ayd.mechanic_workshop.features.auth.repository;

import com.project.ayd.mechanic_workshop.features.auth.entity.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GenderRepository extends JpaRepository<Gender, Long> {

    Optional<Gender> findByName(String name);

    boolean existsByName(String name);
}