package com.project.ayd.mechanic_workshop.features.users.service;

import com.project.ayd.mechanic_workshop.features.auth.entity.User;

public interface UserValidationService {

    void validateUserCanBeDeleted(User user);

    void validateUserCanBeDeactivated(User user);

    void validatePasswordStrength(String password);

    boolean isLastActiveAdmin(User user);

    void validateSpecializationAssignment(Long userId, Long specializationId);
}