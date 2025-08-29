package com.project.ayd.mechanic_workshop.features.workorders.repository;

import com.project.ayd.mechanic_workshop.features.workorders.entity.Part;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartRepository extends JpaRepository<Part, Long> {

    @Query("SELECT p FROM Part p LEFT JOIN FETCH p.category WHERE p.id = :id")
    Optional<Part> findByIdWithCategory(@Param("id") Long id);

    @Query("SELECT p FROM Part p WHERE p.name = :partNumber")
    Optional<Part> findByPartNumber(@Param("partNumber") String partNumber);

    @Query("SELECT p FROM Part p ORDER BY p.name ASC")
    List<Part> findAllActiveOrderByNameAsc();

    @Query("SELECT p FROM Part p WHERE p.category.id = :categoryId ORDER BY p.name ASC")
    List<Part> findByCategoryIdAndIsActiveTrueOrderByNameAsc(@Param("categoryId") Long categoryId);

    @Query("SELECT p FROM Part p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Part> findByNameContainingIgnoreCaseAndIsActiveTrue(@Param("name") String name, Pageable pageable);

    @Query("SELECT p FROM Part p ORDER BY p.name ASC")
    Page<Part> findByIsActiveTrueOrderByNameAsc(Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Part p WHERE p.name = :partNumber")
    Boolean existsByPartNumber(@Param("partNumber") String partNumber);

    @Query("SELECT COUNT(p) FROM Part p")
    Long countActiveparts();
}