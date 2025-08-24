package com.project.ayd.mechanic_workshop.features.users.controller;

import com.project.ayd.mechanic_workshop.features.users.dto.CreateUserRequest;
import com.project.ayd.mechanic_workshop.features.users.dto.PasswordChangeRequest;
import com.project.ayd.mechanic_workshop.features.users.dto.UpdateUserRequest;
import com.project.ayd.mechanic_workshop.features.users.dto.UserResponse;
import com.project.ayd.mechanic_workshop.features.users.service.UserService;
import com.project.ayd.mechanic_workshop.features.users.dto.PasswordChangeRequest;
import com.project.ayd.mechanic_workshop.features.auth.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class AdminController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<UserResponse>> getAllUsersPaginated(Pageable pageable) {
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        UserResponse user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/cui/{cui}")
    public ResponseEntity<UserResponse> getUserByCui(@PathVariable String cui) {
        UserResponse user = userService.getUserByCui(cui);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/type/{userType}")
    public ResponseEntity<List<UserResponse>> getUsersByType(@PathVariable String userType) {
        List<UserResponse> users = userService.getUsersByType(userType);
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse user = userService.updateUser(userId, request);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    @PutMapping("/{userId}/activate")
    public ResponseEntity<Map<String, String>> activateUser(@PathVariable Long userId) {
        userService.activateUser(userId);
        return ResponseEntity.ok(Map.of("message", "User activated successfully"));
    }

    @PutMapping("/{userId}/deactivate")
    public ResponseEntity<Map<String, String>> deactivateUser(@PathVariable Long userId) {
        userService.deactivateUser(userId);
        return ResponseEntity.ok(Map.of("message", "User deactivated successfully"));
    }

    @PostMapping("/{userId}/change-password")
    public ResponseEntity<Map<String, String>> changeUserPassword(
            @PathVariable Long userId,
            @Valid @RequestBody PasswordChangeRequest request) {

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        userService.changeUserPassword(userId, request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    @PostMapping("/{userId}/reset-password")
    public ResponseEntity<Map<String, String>> resetUserPassword(@PathVariable Long userId) {
        userService.resetUserPassword(userId);
        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> searchUsers(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive) {

        if (email != null && !email.trim().isEmpty()) {
            List<UserResponse> users = userService.searchUsersByEmail(email);
            return ResponseEntity.ok(users);
        }

        if (name != null && !name.trim().isEmpty()) {
            List<UserResponse> users = userService.searchUsersByName(name);
            return ResponseEntity.ok(users);
        }

        if (isActive != null) {
            List<UserResponse> users = userService.getUsersByStatus(isActive);
            return ResponseEntity.ok(users);
        }

        throw new IllegalArgumentException("At least one search parameter is required");
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getUserStatistics() {
        Long totalUsers = userRepository.count();
        Long activeUsers = userRepository.countActiveUsersByType(""); // Modificar query
        Long totalAdmins = userRepository.countActiveUsersByType("Administrador");
        Long totalEmployees = userRepository.countActiveUsersByType("Empleado");
        Long totalClients = userRepository.countActiveUsersByType("Cliente");

        Map<String, Object> stats = Map.of(
                "totalUsers", totalUsers,
                "activeUsers", activeUsers,
                "totalAdmins", totalAdmins,
                "totalEmployees", totalEmployees,
                "totalClients", totalClients);

        return ResponseEntity.ok(stats);
    }
}