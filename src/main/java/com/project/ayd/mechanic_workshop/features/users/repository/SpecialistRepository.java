package com.project.ayd.mechanic_workshop.features.users.repository;

import com.project.ayd.mechanic_workshop.features.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpecialistRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.userType.name = 'Especialista' AND u.isActive = true")
    List<User> findAllActiveSpecialists();

    @Query("SELECT u FROM User u WHERE u.userType.name = 'Especialista'")
    List<User> findAllSpecialists();

    @Query("SELECT u FROM User u WHERE u.userType.name = 'Especialista' AND u.id = :id")
    Optional<User> findSpecialistById(Long id);

    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN EmployeeSpecialization es ON u.id = es.user.id " +
            "WHERE u.userType.name = 'Especialista' " +
            "AND es.specializationType.id = :specializationTypeId " +
            "AND es.isActive = true " +
            "AND u.isActive = true")
    List<User> findSpecialistsBySpecializationType(@Param("specializationTypeId") Long specializationTypeId);
}