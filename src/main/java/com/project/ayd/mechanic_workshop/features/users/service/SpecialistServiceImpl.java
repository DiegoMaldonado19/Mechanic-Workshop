package com.project.ayd.mechanic_workshop.features.users.service;

import com.project.ayd.mechanic_workshop.features.auth.entity.Gender;
import com.project.ayd.mechanic_workshop.features.auth.entity.Person;
import com.project.ayd.mechanic_workshop.features.auth.entity.User;
import com.project.ayd.mechanic_workshop.features.auth.entity.UserType;
import com.project.ayd.mechanic_workshop.features.auth.repository.GenderRepository;
import com.project.ayd.mechanic_workshop.features.auth.repository.PersonRepository;
import com.project.ayd.mechanic_workshop.features.auth.repository.UserTypeRepository;
import com.project.ayd.mechanic_workshop.features.users.dto.EmployeeSpecializationRequest;
import com.project.ayd.mechanic_workshop.features.users.dto.SpecialistRequest;
import com.project.ayd.mechanic_workshop.features.users.dto.UserResponse;
import com.project.ayd.mechanic_workshop.features.users.entity.EmployeeSpecialization;
import com.project.ayd.mechanic_workshop.features.users.entity.SpecializationType;
import com.project.ayd.mechanic_workshop.features.users.repository.EmployeeSpecializationRepository;
import com.project.ayd.mechanic_workshop.features.users.repository.SpecializationTypeRepository;
import com.project.ayd.mechanic_workshop.features.users.repository.SpecialistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpecialistServiceImpl implements SpecialistService {

    private final SpecialistRepository specialistRepository;
    private final PersonRepository personRepository;
    private final UserTypeRepository userTypeRepository;
    private final GenderRepository genderRepository;
    private final EmployeeSpecializationRepository employeeSpecializationRepository;
    private final SpecializationTypeRepository specializationTypeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse createSpecialist(SpecialistRequest request) {
        validateSpecialistRequest(request);

        Person person = createPersonFromRequest(request);
        person = personRepository.save(person);

        User specialist = createSpecialistFromRequest(request, person);
        specialist = specialistRepository.save(specialist);

        createSpecialistSpecializations(specialist, request.getSpecializations());

        log.info("Specialist created successfully with ID: {}", specialist.getId());
        return mapToUserResponse(specialist);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllSpecialists() {
        return specialistRepository.findAllSpecialists().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllActiveSpecialists() {
        return specialistRepository.findAllActiveSpecialists().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getSpecialistById(Long specialistId) {
        User specialist = specialistRepository.findSpecialistById(specialistId)
                .orElseThrow(() -> new IllegalArgumentException("Specialist not found with ID: " + specialistId));
        return mapToUserResponse(specialist);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getSpecialistsBySpecializationType(Long specializationTypeId) {
        return specialistRepository.findSpecialistsBySpecializationType(specializationTypeId).stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    private void validateSpecialistRequest(SpecialistRequest request) {
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

    private Person createPersonFromRequest(SpecialistRequest request) {
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

    private User createSpecialistFromRequest(SpecialistRequest request, Person person) {
        UserType userType = userTypeRepository.findByName("Especialista")
                .orElseThrow(() -> new IllegalArgumentException("Especialista user type not found"));

        User user = new User();
        user.setPerson(person);
        user.setUserType(userType);
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setIsActive(true);

        return user;
    }

    private void createSpecialistSpecializations(User specialist,
            List<EmployeeSpecializationRequest> specializationRequests) {
        for (EmployeeSpecializationRequest request : specializationRequests) {
            SpecializationType specializationType = specializationTypeRepository
                    .findById(request.getSpecializationTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid specialization type ID"));

            EmployeeSpecialization specialization = new EmployeeSpecialization();
            specialization.setUser(specialist);
            specialization.setSpecializationType(specializationType);
            specialization.setCertificationDate(request.getCertificationDate());
            specialization.setIsActive(request.getIsActive());

            employeeSpecializationRepository.save(specialization);
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