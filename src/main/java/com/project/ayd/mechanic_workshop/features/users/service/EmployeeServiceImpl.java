package com.project.ayd.mechanic_workshop.features.users.service;

import com.project.ayd.mechanic_workshop.features.auth.entity.Gender;
import com.project.ayd.mechanic_workshop.features.auth.entity.Person;
import com.project.ayd.mechanic_workshop.features.auth.entity.User;
import com.project.ayd.mechanic_workshop.features.auth.entity.UserType;
import com.project.ayd.mechanic_workshop.features.auth.repository.GenderRepository;
import com.project.ayd.mechanic_workshop.features.auth.repository.PersonRepository;
import com.project.ayd.mechanic_workshop.features.auth.repository.UserTypeRepository;
import com.project.ayd.mechanic_workshop.features.users.dto.EmployeeRequest;
import com.project.ayd.mechanic_workshop.features.users.dto.EmployeeSpecializationRequest;
import com.project.ayd.mechanic_workshop.features.users.dto.UserResponse;
import com.project.ayd.mechanic_workshop.features.users.entity.EmployeeSpecialization;
import com.project.ayd.mechanic_workshop.features.users.entity.SpecializationType;
import com.project.ayd.mechanic_workshop.features.users.repository.EmployeeRepository;
import com.project.ayd.mechanic_workshop.features.users.repository.EmployeeSpecializationRepository;
import com.project.ayd.mechanic_workshop.features.users.repository.SpecializationTypeRepository;
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
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PersonRepository personRepository;
    private final UserTypeRepository userTypeRepository;
    private final GenderRepository genderRepository;
    private final EmployeeSpecializationRepository employeeSpecializationRepository;
    private final SpecializationTypeRepository specializationTypeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse createEmployee(EmployeeRequest request) {
        validateEmployeeRequest(request);

        Person person = createPersonFromRequest(request);
        person = personRepository.save(person);

        User employee = createEmployeeFromRequest(request, person);
        employee = employeeRepository.save(employee);

        if (request.getSpecializations() != null && !request.getSpecializations().isEmpty()) {
            createEmployeeSpecializations(employee, request.getSpecializations());
        }

        log.info("Employee created successfully with ID: {}", employee.getId());
        return mapToUserResponse(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllEmployees() {
        return employeeRepository.findAllEmployees().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllActiveEmployees() {
        return employeeRepository.findAllActiveEmployees().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getEmployeeById(Long employeeId) {
        User employee = employeeRepository.findEmployeeById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + employeeId));
        return mapToUserResponse(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getEmployeeByCui(String cui) {
        User employee = employeeRepository.findEmployeeByCui(cui)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with CUI: " + cui));
        return mapToUserResponse(employee);
    }

    @Override
    @Transactional
    public void addSpecializationToEmployee(Long employeeId, EmployeeSpecializationRequest request) {
        User employee = employeeRepository.findEmployeeById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + employeeId));

        if (employeeSpecializationRepository.existsByUserIdAndSpecializationTypeIdAndIsActiveTrue(
                employeeId, request.getSpecializationTypeId())) {
            throw new IllegalArgumentException("Employee already has this specialization");
        }

        SpecializationType specializationType = specializationTypeRepository.findById(request.getSpecializationTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid specialization type ID"));

        EmployeeSpecialization specialization = new EmployeeSpecialization();
        specialization.setUser(employee);
        specialization.setSpecializationType(specializationType);
        specialization.setCertificationDate(request.getCertificationDate());
        specialization.setIsActive(request.getIsActive());

        employeeSpecializationRepository.save(specialization);
        log.info("Specialization added to employee ID: {}", employeeId);
    }

    @Override
    @Transactional
    public void removeSpecializationFromEmployee(Long employeeId, Long specializationId) {
        EmployeeSpecialization specialization = employeeSpecializationRepository.findById(specializationId)
                .orElseThrow(
                        () -> new IllegalArgumentException("Specialization not found with ID: " + specializationId));

        if (!specialization.getUser().getId().equals(employeeId)) {
            throw new IllegalArgumentException("Specialization does not belong to this employee");
        }

        specialization.setIsActive(false);
        employeeSpecializationRepository.save(specialization);
        log.info("Specialization removed from employee ID: {}", employeeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getEmployeesBySpecialization(Long specializationTypeId) {
        List<EmployeeSpecialization> specializations = employeeSpecializationRepository
                .findBySpecializationTypeIdAndIsActiveTrue(specializationTypeId);

        return specializations.stream()
                .map(spec -> mapToUserResponse(spec.getUser()))
                .collect(Collectors.toList());
    }

    private void validateEmployeeRequest(EmployeeRequest request) {
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

    private Person createPersonFromRequest(EmployeeRequest request) {
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

    private User createEmployeeFromRequest(EmployeeRequest request, Person person) {
        UserType userType = userTypeRepository.findByName("Empleado")
                .orElseThrow(() -> new IllegalArgumentException("Empleado user type not found"));

        User user = new User();
        user.setPerson(person);
        user.setUserType(userType);
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setIsActive(true);

        return user;
    }

    private void createEmployeeSpecializations(User employee,
            List<EmployeeSpecializationRequest> specializationRequests) {
        for (EmployeeSpecializationRequest request : specializationRequests) {
            SpecializationType specializationType = specializationTypeRepository
                    .findById(request.getSpecializationTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid specialization type ID"));

            EmployeeSpecialization specialization = new EmployeeSpecialization();
            specialization.setUser(employee);
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