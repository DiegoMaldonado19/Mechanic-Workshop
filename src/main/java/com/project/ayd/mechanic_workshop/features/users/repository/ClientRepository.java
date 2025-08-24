package com.project.ayd.mechanic_workshop.features.users.repository;

import com.project.ayd.mechanic_workshop.features.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.userType.name = 'Cliente' AND u.isActive = true")
    List<User> findAllActiveClients();

    @Query("SELECT u FROM User u WHERE u.userType.name = 'Cliente'")
    List<User> findAllClients();

    @Query("SELECT u FROM User u WHERE u.userType.name = 'Cliente' AND u.person.cui = :cui")
    Optional<User> findClientByCui(String cui);

    @Query("SELECT u FROM User u WHERE u.userType.name = 'Cliente' AND u.person.email = :email")
    Optional<User> findClientByEmail(String email);
}