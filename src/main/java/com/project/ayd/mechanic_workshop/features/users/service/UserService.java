package com.project.ayd.mechanic_workshop.features.users.service;

import com.project.ayd.mechanic_workshop.features.users.dto.CreateUserRequest;
import com.project.ayd.mechanic_workshop.features.users.dto.UpdateUserRequest;
import com.project.ayd.mechanic_workshop.features.users.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse updateUser(Long userId, UpdateUserRequest request);

    UserResponse getUserById(Long userId);

    UserResponse getUserByCui(String cui);

    List<UserResponse> getAllUsers();

    Page<UserResponse> getAllUsers(Pageable pageable);

    List<UserResponse> getUsersByType(String userType);

    void deleteUser(Long userId);

    void activateUser(Long userId);

    void deactivateUser(Long userId);
}