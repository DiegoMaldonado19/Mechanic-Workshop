package com.project.ayd.mechanic_workshop.features.users.service;

import com.project.ayd.mechanic_workshop.features.auth.entity.User;
import com.project.ayd.mechanic_workshop.features.auth.repository.UserRepository;
import com.project.ayd.mechanic_workshop.features.users.repository.EmployeeSpecializationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserValidationServiceImpl implements UserValidationService {

    private final UserRepository userRepository;
    private final EmployeeSpecializationRepository employeeSpecializationRepository;

    @Override
    public void validateUserCanBeDeleted(User user) {
        // Verificar si es el último administrador activo
        if (isLastActiveAdmin(user)) {
            throw new IllegalArgumentException("Cannot delete the last active administrator");
        }

        // Verificar si tiene trabajos activos (esto se implementará en el módulo de
        // workorders)
        // Por ahora solo validamos el rol de administrador
        log.info("User validation passed for deletion: {}", user.getUsername());
    }

    @Override
    public void validateUserCanBeDeactivated(User user) {
        if (isLastActiveAdmin(user)) {
            throw new IllegalArgumentException("Cannot deactivate the last active administrator");
        }
        log.info("User validation passed for deactivation: {}", user.getUsername());
    }

    @Override
    public void validatePasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }

        // Al menos una letra mayúscula
        if (!Pattern.compile("[A-Z]").matcher(password).find()) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }

        // Al menos una letra minúscula
        if (!Pattern.compile("[a-z]").matcher(password).find()) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter");
        }

        // Al menos un número
        if (!Pattern.compile("[0-9]").matcher(password).find()) {
            throw new IllegalArgumentException("Password must contain at least one number");
        }
    }

    @Override
    public boolean isLastActiveAdmin(User user) {
        if (!"Administrador".equals(user.getUserType().getName())) {
            return false;
        }

        Long activeAdmins = userRepository.countActiveUsersByType("Administrador");
        return activeAdmins <= 1;
    }

    @Override
    public void validateSpecializationAssignment(Long userId, Long specializationId) {
        if (employeeSpecializationRepository.existsByUserIdAndSpecializationTypeIdAndIsActiveTrue(userId,
                specializationId)) {
            throw new IllegalArgumentException("User already has this specialization active");
        }

        // Validar límite máximo de especializaciones (ej: máximo 5)
        Long currentSpecializations = employeeSpecializationRepository.countActiveSpecializationsByUser(userId);
        if (currentSpecializations >= 5) {
            throw new IllegalArgumentException("User cannot have more than 5 active specializations");
        }
    }
}