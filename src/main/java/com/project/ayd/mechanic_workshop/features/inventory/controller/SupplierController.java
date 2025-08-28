package com.project.ayd.mechanic_workshop.features.inventory.controller;

import com.project.ayd.mechanic_workshop.features.inventory.dto.SupplierRequest;
import com.project.ayd.mechanic_workshop.features.inventory.dto.SupplierResponse;
import com.project.ayd.mechanic_workshop.features.inventory.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inventory/suppliers")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO')")
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<SupplierResponse> createSupplier(@Valid @RequestBody SupplierRequest request) {
        SupplierResponse response = supplierService.createSupplier(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{supplierId}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<SupplierResponse> updateSupplier(
            @PathVariable Long supplierId,
            @Valid @RequestBody SupplierRequest request) {
        SupplierResponse response = supplierService.updateSupplier(supplierId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{supplierId}")
    public ResponseEntity<SupplierResponse> getSupplierById(@PathVariable Long supplierId) {
        SupplierResponse response = supplierService.getSupplierById(supplierId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<SupplierResponse>> getAllActiveSuppliers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "companyName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SupplierResponse> response = supplierService.getAllActiveSuppliers(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<SupplierResponse>> searchSuppliers(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("companyName").ascending());
        Page<SupplierResponse> response = supplierService.searchSuppliers(searchTerm, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dropdown")
    public ResponseEntity<List<SupplierResponse>> getAllActiveSuppliersForDropdown() {
        List<SupplierResponse> response = supplierService.getAllActiveSuppliersForDropdown();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{supplierId}/deactivate")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<SupplierResponse> deactivateSupplier(@PathVariable Long supplierId) {
        SupplierResponse response = supplierService.deactivateSupplier(supplierId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{supplierId}/activate")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<SupplierResponse> activateSupplier(@PathVariable Long supplierId) {
        SupplierResponse response = supplierService.activateSupplier(supplierId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/exists/cui/{cui}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Boolean>> checkIfSupplierExistsByCui(@PathVariable String cui) {
        boolean exists = supplierService.existsByPersonCui(cui);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @GetMapping("/exists/email/{email}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Boolean>> checkIfSupplierExistsByEmail(@PathVariable String email) {
        boolean exists = supplierService.existsByContactEmail(email);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Long>> countActiveSuppliers() {
        Long count = supplierService.countActiveSuppliers();
        return ResponseEntity.ok(Map.of("count", count));
    }
}