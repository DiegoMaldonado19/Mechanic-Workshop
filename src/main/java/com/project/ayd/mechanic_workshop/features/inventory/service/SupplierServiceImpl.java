package com.project.ayd.mechanic_workshop.features.inventory.service;

import com.project.ayd.mechanic_workshop.features.auth.entity.Person;
import com.project.ayd.mechanic_workshop.features.auth.repository.PersonRepository;
import com.project.ayd.mechanic_workshop.features.inventory.dto.SupplierRequest;
import com.project.ayd.mechanic_workshop.features.inventory.dto.SupplierResponse;
import com.project.ayd.mechanic_workshop.features.inventory.entity.Supplier;
import com.project.ayd.mechanic_workshop.features.inventory.repository.SupplierRepository;
import com.project.ayd.mechanic_workshop.features.shared.entity.AddressDetail;
import com.project.ayd.mechanic_workshop.features.shared.repository.AddressDetailRepository;
import com.project.ayd.mechanic_workshop.features.users.dto.PersonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final PersonRepository personRepository;
    private final AddressDetailRepository addressDetailRepository;

    @Override
    @Transactional
    public SupplierResponse createSupplier(SupplierRequest request) {
        validateSupplierRequest(request);

        Supplier supplier = Supplier.builder()
                .companyName(request.getCompanyName())
                .contactEmail(request.getContactEmail())
                .contactPhone(request.getContactPhone())
                .build();

        if (request.getPersonCui() != null) {
            Person person = personRepository.findById(request.getPersonCui())
                    .orElseThrow(
                            () -> new IllegalArgumentException("Person not found with CUI: " + request.getPersonCui()));
            supplier.setPerson(person);
        }

        if (request.getAddressDetailId() != null) {
            AddressDetail addressDetail = addressDetailRepository.findById(request.getAddressDetailId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Address detail not found with ID: " + request.getAddressDetailId()));
            supplier.setAddressDetail(addressDetail);
        }

        supplier = supplierRepository.save(supplier);

        log.info("Supplier created successfully with ID: {}", supplier.getId());

        return mapToSupplierResponse(supplier);
    }

    @Override
    @Transactional
    public SupplierResponse updateSupplier(Long supplierId, SupplierRequest request) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found with ID: " + supplierId));

        validateSupplierRequestForUpdate(request, supplier);

        supplier.setCompanyName(request.getCompanyName());
        supplier.setContactEmail(request.getContactEmail());
        supplier.setContactPhone(request.getContactPhone());

        if (request.getPersonCui() != null) {
            Person person = personRepository.findById(request.getPersonCui())
                    .orElseThrow(
                            () -> new IllegalArgumentException("Person not found with CUI: " + request.getPersonCui()));
            supplier.setPerson(person);
        }

        if (request.getAddressDetailId() != null) {
            AddressDetail addressDetail = addressDetailRepository.findById(request.getAddressDetailId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Address detail not found with ID: " + request.getAddressDetailId()));
            supplier.setAddressDetail(addressDetail);
        }

        supplier = supplierRepository.save(supplier);

        log.info("Supplier updated successfully with ID: {}", supplierId);

        return mapToSupplierResponse(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierResponse getSupplierById(Long supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found with ID: " + supplierId));
        return mapToSupplierResponse(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplierResponse> getAllActiveSuppliers(Pageable pageable) {
        return supplierRepository.findByIsActiveTrue(pageable)
                .map(this::mapToSupplierResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplierResponse> searchSuppliers(String searchTerm, Pageable pageable) {
        return supplierRepository.findActiveBySearchTerm(searchTerm, pageable)
                .map(this::mapToSupplierResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponse> getAllActiveSuppliersForDropdown() {
        return supplierRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToSupplierResponse)
                .toList();
    }

    @Override
    @Transactional
    public SupplierResponse deactivateSupplier(Long supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found with ID: " + supplierId));

        supplier.setIsActive(false);
        supplier = supplierRepository.save(supplier);

        log.info("Supplier deactivated with ID: {}", supplierId);

        return mapToSupplierResponse(supplier);
    }

    @Override
    @Transactional
    public SupplierResponse activateSupplier(Long supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found with ID: " + supplierId));

        supplier.setIsActive(true);
        supplier = supplierRepository.save(supplier);

        log.info("Supplier activated with ID: {}", supplierId);

        return mapToSupplierResponse(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByPersonCui(String cui) {
        return supplierRepository.existsByPersonCui(cui);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByContactEmail(String contactEmail) {
        return supplierRepository.existsByContactEmail(contactEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countActiveSuppliers() {
        return supplierRepository.countActiveSuppliers();
    }

    private void validateSupplierRequest(SupplierRequest request) {
        if (request.getPersonCui() == null && request.getCompanyName() == null) {
            throw new IllegalArgumentException("Either person CUI or company name must be provided");
        }

        if (request.getPersonCui() != null && existsByPersonCui(request.getPersonCui())) {
            throw new IllegalArgumentException(
                    "Supplier already exists for person with CUI: " + request.getPersonCui());
        }

        if (request.getContactEmail() != null && existsByContactEmail(request.getContactEmail())) {
            throw new IllegalArgumentException("Supplier already exists with email: " + request.getContactEmail());
        }
    }

    private void validateSupplierRequestForUpdate(SupplierRequest request, Supplier existingSupplier) {
        if (request.getPersonCui() == null && request.getCompanyName() == null) {
            throw new IllegalArgumentException("Either person CUI or company name must be provided");
        }

        if (request.getPersonCui() != null && 
            !request.getPersonCui().equals(existingSupplier.getPerson()?.getCui()) && 
            existsByPersonCui(request.getPersonCui())) {
            throw new IllegalArgumentException("Supplier already exists for person with CUI: " + request.getPersonCui());
        }

        if (request.getContactEmail() != null && 
            !request.getContactEmail().equals(existingSupplier.getContactEmail()) && 
            existsByContactEmail(request.getContactEmail())) {
            throw new IllegalArgumentException("Supplier already exists with email: " + request.getContactEmail());
        }
    }

    private SupplierResponse mapToSupplierResponse(Supplier supplier) {
        PersonResponse personResponse = null;
        if (supplier.getPerson() != null) {
            personResponse = PersonResponse.builder()
                    .cui(supplier.getPerson().getCui())
                    .nit(supplier.getPerson().getNit())
                    .firstName(supplier.getPerson().getFirstName())
                    .lastName(supplier.getPerson().getLastName())
                    .email(supplier.getPerson().getEmail())
                    .phone(supplier.getPerson().getPhone())
                    .birthDate(supplier.getPerson().getBirthDate())
                    .createdAt(supplier.getPerson().getCreatedAt())
                    .updatedAt(supplier.getPerson().getUpdatedAt())
                    .build();
        }

        return SupplierResponse.builder()
                .id(supplier.getId())
                .person(personResponse)
                .companyName(supplier.getCompanyName())
                .contactEmail(supplier.getContactEmail())
                .contactPhone(supplier.getContactPhone())
                .isActive(supplier.getIsActive())
                .createdAt(supplier.getCreatedAt())
                .updatedAt(supplier.getUpdatedAt())
                .build();
    }
}