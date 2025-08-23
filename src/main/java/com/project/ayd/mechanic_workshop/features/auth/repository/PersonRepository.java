package com.project.ayd.mechanic_workshop.features.auth.repository;

import com.project.ayd.mechanic_workshop.features.auth.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, String> {

    Optional<Person> findByEmail(String email);

    Optional<Person> findByNit(String nit);

    boolean existsByEmail(String email);

    boolean existsByNit(String nit);
}