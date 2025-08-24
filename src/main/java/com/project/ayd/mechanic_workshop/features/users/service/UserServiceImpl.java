package com.project.ayd.mechanic_workshop.features.users.service;

import com.project.ayd.mechanic_workshop.features.auth.entity.Gender;
import com.project.ayd.mechanic_workshop.features.auth.entity.Person;
import com.project.ayd.mechanic_workshop.features.auth.entity.User;
import com.project.ayd.mechanic_workshop.features.auth.entity.UserType;
import com.project.ayd.mechanic_workshop.features.auth.repository.GenderRepository;
import com.project.ayd.mechanic_workshop.features.auth.repository.PersonRepository;
import com.project.ayd.mechanic_workshop.features.auth.repository.UserRepository;
import com.project.ayd.mechanic_workshop.features.auth.repository.UserTypeRepository;
import com.project.ayd.mechanic_workshop.features.users.dto.CreateUserRequest;
import com.project.ayd.mechanic_workshop.features.users.dto.UpdateUserRequest;
import com.project.ayd.mechanic_workshop.features.users.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PersonRepository personRepository;
    private final UserTypeRepository userTypeRepository;
    private final GenderRepository genderRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        validateCreateUserRequest(request);

        Person person = createPersonFromRequest(request);
        person = personRepository.save(person);

        User user = createUserFromRequest(request, person);
        user = userRepository.save(user);

        log.info("User created successfully with ID: {}", user.getId());
        return mapToUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        updatePersonFromRequest(user.getPerson(), request);
        personRepository.save(user.getPerson());

        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
            userRepository.save(user);
        }

        log.info("User updated successfully with ID: {}", userId);
        return mapToUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        return mapToUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByCui(String cui) {
        User user = userRepository.findByPersonCui(cui)
                .orElseThrow(() -> new IllegalArgumentException("User not found with CUI: " + cui));
        return mapToUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::mapToUserResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByType(String userType) {
        return userRepository.findByUserTypeName(userType).stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        userRepository.delete(user);
        log.info("User deleted successfully with ID: {}", userId);
    }

    @Override
    @Transactional
    public void activateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        user.setIsActive(true);
        userRepository.save(user);
        log.info("User activated successfully with ID: {}", userId);
    }

    @Override
    @Transactional
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        user.setIsActive(false);
        userRepository.save(user);
        log.info("User deactivated successfully with ID: {}", userId);
    }

    private void validateCreateUserRequest(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (personRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }
        if (personRepository.existsByCui(request.getCui())) {
            throw new IllegalArgumentException("CUI is already registered");
        }
        if (personRepository.existsByNit(request.getNit())) {
            throw new IllegalArgumentException("NIT is already registered");
        }
    }

    private Person createPersonFromRequest(CreateUserRequest request) {
        Person person = new Person();
        person.setCui(request.getCui());
        person.setNit(request.getNit());
        person.setFirstName(request.getFirstName());
        person.setLastName(request.getLastName());
        person.setEmail(request.getEmail());
        person.setPhone(request.getPhone());
        person.setBirthDate(request.getBirthDate());

        if (request.getGenderId() != null) {
            Gender gender = genderRepository.findById(request.getGenderId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid gender ID"));
            person.setGender(gender);
        }

        return person;
    }

    private User createUserFromRequest(CreateUserRequest request, Person person) {
        UserType userType = userTypeRepository.findById(request.getUserTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user type ID"));

        User user = new User();
        user.setPerson(person);
        user.setUserType(userType);
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setIsActive(true);

        return user;
    }

    private void updatePersonFromRequest(Person person, UpdateUserRequest request) {
        if (request.getNit() != null) {
            person.setNit(request.getNit());
        }
        if (request.getFirstName() != null) {
            person.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            person.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            person.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            person.setPhone(request.getPhone());
        }
        if (request.getBirthDate() != null) {
            person.setBirthDate(request.getBirthDate());
        }
        if (request.getGenderId() != null) {
            Gender gender = genderRepository.findById(request.getGenderId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid gender ID"));
            person.setGender(gender);
        }
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .cui(user.getPerson().getCui())
                .nit(user.getPerson().getNit())
                .firstName(user.getPerson().getFirstName())
                .lastName(user.getPerson().getLastName())
                .email(user.getPerson().getEmail())
                .phone(user.getPerson().getPhone())
                .username(user.getUsername())
                .userType(user.getUserType().getName())
                .gender(user.getPerson().getGender() != null ? user.getPerson().getGender().getName() : null)
                .isActive(user.getIsActive())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .build();
    }
}