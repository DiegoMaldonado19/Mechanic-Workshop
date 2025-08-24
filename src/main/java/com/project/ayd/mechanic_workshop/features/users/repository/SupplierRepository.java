package com.project.ayd.mechanic_workshop.features.users.repository;

import com.project.ayd.mechanic_workshop.features.users.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    List<Supplier> findByIsActiveTrue();

    Optional<Supplier> findByPersonCui(String cui);

    @Query("SELECT s FROM Supplier s WHERE s.contactEmail = :email")
    Optional<Supplier> findByContactEmail(@Param("email") String email);

    @Query("SELECT s FROM Supplier s WHERE s.companyName LIKE %:name% AND s.isActive = true")
    List<Supplier> findByCompanyNameContainingAndIsActiveTrue(@Param("name") String name);

    boolean existsByPersonCui(String cui);

    boolean existsByContactEmail(String email);

    @Query("SELECT s FROM Supplier s WHERE s.person.firstName LIKE %:name% OR s.person.lastName LIKE %:name% OR s.companyName LIKE %:name%")
    List<Supplier> findByAnyNameContaining(@Param("name") String name);

    @Query("SELECT COUNT(s) FROM Supplier s WHERE s.isActive = true")
    Long countActiveSuppliers();
}