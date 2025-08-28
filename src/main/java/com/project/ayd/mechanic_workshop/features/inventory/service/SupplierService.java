package com.project.ayd.mechanic_workshop.features.inventory.service;

import com.project.ayd.mechanic_workshop.features.inventory.dto.SupplierRequest;
import com.project.ayd.mechanic_workshop.features.inventory.dto.SupplierResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SupplierService {

    SupplierResponse createSupplier(SupplierRequest request);

    SupplierResponse updateSupplier(Long supplierId, SupplierRequest request);

    SupplierResponse getSupplierById(Long supplierId);

    Page<SupplierResponse> getAllActiveSuppliers(Pageable pageable);

    Page<SupplierResponse> searchSuppliers(String searchTerm, Pageable pageable);

    List<SupplierResponse> getAllActiveSuppliersForDropdown();

    SupplierResponse deactivateSupplier(Long supplierId);

    SupplierResponse activateSupplier(Long supplierId);

    boolean existsByPersonCui(String cui);

    boolean existsByContactEmail(String contactEmail);

    Long countActiveSuppliers();
}