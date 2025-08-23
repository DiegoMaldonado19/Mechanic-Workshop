package com.project.ayd.mechanic_workshop.features.auth.repository;

import com.project.ayd.mechanic_workshop.features.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByPersonEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByPersonCui(String cui);

    boolean existsByPersonNit(String nit);

    boolean existsByPersonEmail(String email);

    @Query("SELECT u FROM User u WHERE u.username = :username AND u.isActive = true")
    Optional<User> findActiveByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u WHERE u.person.email = :email AND u.isActive = true")
    Optional<User> findActiveByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.lockedUntil IS NOT NULL AND u.lockedUntil < :now")
    Iterable<User> findUsersToUnlock(@Param("now") LocalDateTime now);
}