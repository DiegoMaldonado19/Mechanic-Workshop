package com.project.ayd.mechanic_workshop.features.users.service;

import com.project.ayd.mechanic_workshop.features.auth.entity.Gender;
import com.project.ayd.mechanic_workshop.features.auth.entity.Person;
import com.project.ayd.mechanic_workshop.features.auth.entity.User;
import com.project.ayd.mechanic_workshop.features.auth.entity.UserType;
import com.project.ayd.mechanic_workshop.features.auth.repository.GenderRepository;
import com.project.ayd.mechanic_workshop.features.auth.repository.PersonRepository;
import com.project.ayd.mechanic_workshop.features.auth.repository.UserTypeRepository;
import com.project.ayd.mechanic_workshop.features.users.dto.ClientRequest;
import com.project.ayd.mechanic_workshop.features.users.dto.UserResponse;
import com.project.ayd.mechanic_workshop.features.users.repository.ClientRepository;
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
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final PersonRepository personRepository;
    private final UserTypeRepository userTypeRepository;
    private final GenderRepository genderRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse createClient(ClientRequest request) {
        validateClientRequest(request);

        Person person = createPersonFromRequest(request);
        person = personRepository.save(person);

        User client = createClientFromRequest(request, person);
        client = clientRepository.save(client);

        log.info("Client created successfully with ID: {}", client.getId());
        return mapToUserResponse(client);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllClients() {
        return clientRepository.findAllClients().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllActiveClients() {
        return clientRepository.findAllActiveClients().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getClientById(Long clientId) {
        User client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found with ID: " + clientId));

        if (!"Cliente".equals(client.getUserType().getName())) {
            throw new IllegalArgumentException("User is not a client");
        }

        return mapToUserResponse(client);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getClientByCui(String cui) {
        User client = clientRepository.findClientByCui(cui)
                .orElseThrow(() -> new IllegalArgumentException("Client not found with CUI: " + cui));
        return mapToUserResponse(client);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getClientByEmail(String email) {
        User client = clientRepository.findClientByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Client not found with email: " + email));
        return mapToUserResponse(client);
    }

    private void validateClientRequest(ClientRequest request) {
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

    private Person createPersonFromRequest(ClientRequest request) {
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

    private User createClientFromRequest(ClientRequest request, Person person) {
        UserType userType = userTypeRepository.findByName("Cliente")
                .orElseThrow(() -> new IllegalArgumentException("Cliente user type not found"));

        User user = new User();
        user.setPerson(person);
        user.setUserType(userType);
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setIsActive(true);

        return user;
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