package com.project.ayd.mechanic_workshop.features.users.repository;

import com.project.ayd.mechanic_workshop.features.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.userType.name = 'Empleado' AND u.isActive = true")
    List<User> findAllActiveEmployees();

    @Query("SELECT u FROM User u WHERE u.userType.name = 'Empleado'")
    List<User> findAllEmployees();

    @Query("SELECT u FROM User u WHERE u.userType.name = 'Empleado' AND u.id = :id")
    Optional<User> findEmployeeById(Long id);

    @Query("SELECT u FROM User u WHERE u.userType.name = 'Empleado' AND u.person.cui = :cui")
    Optional<User> findEmployeeByCui(String cui);
}