package com.project.ayd.mechanic_workshop.features.inventory.repository;

import com.project.ayd.mechanic_workshop.features.inventory.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    List<Supplier> findByIsActiveTrue();

    Page<Supplier> findByIsActiveTrue(Pageable pageable);

    Optional<Supplier> findByPersonCui(String cui);

    @Query("SELECT s FROM Supplier s WHERE s.isActive = true AND (s.companyName ILIKE %:searchTerm% OR s.contactEmail ILIKE %:searchTerm% OR s.person.firstName ILIKE %:searchTerm% OR s.person.lastName ILIKE %:searchTerm%)")
    Page<Supplier> findActiveBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

    boolean existsByPersonCui(String cui);

    boolean existsByContactEmail(String contactEmail);

    @Query("SELECT COUNT(s) FROM Supplier s WHERE s.isActive = true")
    Long countActiveSuppliers();
}