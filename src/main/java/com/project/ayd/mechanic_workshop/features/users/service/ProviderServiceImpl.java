package com.project.ayd.mechanic_workshop.features.users.service;

import com.project.ayd.mechanic_workshop.features.auth.entity.Person;
import com.project.ayd.mechanic_workshop.features.auth.repository.PersonRepository;
import com.project.ayd.mechanic_workshop.features.users.dto.ProviderRequest;
import com.project.ayd.mechanic_workshop.features.users.dto.ProviderUpdateRequest;
import com.project.ayd.mechanic_workshop.features.users.dto.UserResponse;
import com.project.ayd.mechanic_workshop.features.users.entity.Supplier;
import com.project.ayd.mechanic_workshop.features.users.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProviderServiceImpl implements ProviderService {

    private final SupplierRepository supplierRepository;
    private final PersonRepository personRepository;

    @Override
    @Transactional
    public UserResponse createProvider(ProviderRequest request) {
        validateProviderRequest(request);

        Person person = null;
        if (request.getCui() != null) {
            person = createOrGetPersonFromRequest(request);
            person = personRepository.save(person);
        }

        Supplier supplier = createSupplierFromRequest(request, person);
        supplier = supplierRepository.save(supplier);

        log.info("Provider created successfully with ID: {}", supplier.getId());
        return mapToProviderResponse(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllProviders() {
        return supplierRepository.findAll().stream()
                .map(this::mapToProviderResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllActiveProviders() {
        return supplierRepository.findByIsActiveTrue().stream()
                .map(this::mapToProviderResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getProviderById(Long providerId) {
        Supplier supplier = supplierRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found with ID: " + providerId));
        return mapToProviderResponse(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getProviderByCui(String cui) {
        Supplier supplier = supplierRepository.findByPersonCui(cui)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found with CUI: " + cui));
        return mapToProviderResponse(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getProviderByEmail(String email) {
        Supplier supplier = supplierRepository.findByContactEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found with email: " + email));
        return mapToProviderResponse(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> searchProvidersByCompanyName(String companyName) {
        return supplierRepository.findByCompanyNameContainingAndIsActiveTrue(companyName).stream()
                .map(this::mapToProviderResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void activateProvider(Long providerId) {
        Supplier supplier = supplierRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found with ID: " + providerId));

        supplier.setIsActive(true);
        supplierRepository.save(supplier);
        log.info("Provider activated successfully with ID: {}", providerId);
    }

    @Override
    @Transactional
    public void deactivateProvider(Long providerId) {
        Supplier supplier = supplierRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found with ID: " + providerId));

        supplier.setIsActive(false);
        supplierRepository.save(supplier);
        log.info("Provider deactivated successfully with ID: {}", providerId);
    }

    private void validateProviderRequest(ProviderRequest request) {
        if (request.getCui() == null && request.getCompanyName() == null) {
            throw new IllegalArgumentException("Either CUI (for person) or company name must be provided");
        }

        if (request.getCui() != null) {
            if (personRepository.existsByCui(request.getCui())) {
                throw new IllegalArgumentException("CUI is already registered");
            }
            if (request.getNit() != null && personRepository.existsByNit(request.getNit())) {
                throw new IllegalArgumentException("NIT is already registered");
            }
        }

        if (request.getContactEmail() != null && supplierRepository.existsByContactEmail(request.getContactEmail())) {
            throw new IllegalArgumentException("Contact email is already in use");
        }
    }

    private Person createOrGetPersonFromRequest(ProviderRequest request) {
        Person person = new Person();
        person.setCui(request.getCui());
        person.setNit(request.getNit());
        person.setFirstName(request.getFirstName());
        person.setLastName(request.getLastName());
        person.setEmail(request.getContactEmail());
        person.setPhone(request.getContactPhone());
        return person;
    }

    private Supplier createSupplierFromRequest(ProviderRequest request, Person person) {
        Supplier supplier = new Supplier();
        supplier.setPerson(person);
        supplier.setCompanyName(request.getCompanyName());
        supplier.setContactEmail(request.getContactEmail());
        supplier.setContactPhone(request.getContactPhone());
        supplier.setAddressDetailId(request.getAddressDetailId());
        supplier.setIsActive(request.getIsActive());
        return supplier;
    }

    private UserResponse mapToProviderResponse(Supplier supplier) {
        UserResponse.UserResponseBuilder builder = UserResponse.builder()
                .id(supplier.getId())
                .userType("Proveedor")
                .isActive(supplier.getIsActive())
                .createdAt(supplier.getCreatedAt());

        if (supplier.getPerson() != null) {
            Person person = supplier.getPerson();
            builder.cui(person.getCui())
                    .nit(person.getNit())
                    .firstName(person.getFirstName())
                    .lastName(person.getLastName())
                    .email(person.getEmail())
                    .phone(person.getPhone());
        } else {
            builder.firstName(supplier.getCompanyName())
                    .email(supplier.getContactEmail())
                    .phone(supplier.getContactPhone());
        }

        return builder.build();
    }

    @Override
    @Transactional
    public UserResponse updateProvider(Long providerId, ProviderUpdateRequest request) {
        Supplier supplier = supplierRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found with ID: " + providerId));

        // Actualizar datos de persona si existe
        if (supplier.getPerson() != null) {
            Person person = supplier.getPerson();
            if (request.getNit() != null)
                person.setNit(request.getNit());
            if (request.getFirstName() != null)
                person.setFirstName(request.getFirstName());
            if (request.getLastName() != null)
                person.setLastName(request.getLastName());
            if (request.getContactEmail() != null)
                person.setEmail(request.getContactEmail());
            if (request.getContactPhone() != null)
                person.setPhone(request.getContactPhone());
            personRepository.save(person);
        }

        // Actualizar datos del proveedor
        if (request.getCompanyName() != null)
            supplier.setCompanyName(request.getCompanyName());
        if (request.getContactEmail() != null)
            supplier.setContactEmail(request.getContactEmail());
        if (request.getContactPhone() != null)
            supplier.setContactPhone(request.getContactPhone());
        if (request.getAddressDetailId() != null)
            supplier.setAddressDetailId(request.getAddressDetailId());

        supplier = supplierRepository.save(supplier);
        log.info("Provider updated successfully with ID: {}", providerId);

        return mapToProviderResponse(supplier);
    }
}