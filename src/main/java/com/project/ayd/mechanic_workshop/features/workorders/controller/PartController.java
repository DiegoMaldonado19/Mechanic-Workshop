package com.project.ayd.mechanic_workshop.features.workorders.controller;

import com.project.ayd.mechanic_workshop.features.workorders.dto.PartCategoryResponse;
import com.project.ayd.mechanic_workshop.features.workorders.dto.PartResponse;
import com.project.ayd.mechanic_workshop.features.workorders.service.PartService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/parts")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'EMPLEADO', 'ESPECIALISTA')")
public class PartController {

    private final PartService partService;

    @GetMapping("/{partId}")
    public ResponseEntity<PartResponse> getPartById(@PathVariable Long partId) {
        PartResponse response = partService.getPartById(partId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/part-number/{partNumber}")
    public ResponseEntity<PartResponse> getPartByPartNumber(@PathVariable String partNumber) {
        PartResponse response = partService.getPartByPartNumber(partNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<PartResponse>> getAllActiveParts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PartResponse> response = partService.getAllActivePartsPageable(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<List<PartResponse>> getAllActivePartsList() {
        List<PartResponse> response = partService.getAllActiveParts();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<PartResponse>> getPartsByCategory(@PathVariable Long categoryId) {
        List<PartResponse> response = partService.getPartsByCategory(categoryId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PartResponse>> searchPartsByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<PartResponse> response = partService.searchPartsByName(name, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/exists/{partNumber}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Boolean>> checkPartNumberExists(@PathVariable String partNumber) {
        boolean exists = partService.isPartNumberExists(partNumber);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, Long>> countActiveParts() {
        Long count = partService.countActiveParts();
        return ResponseEntity.ok(Map.of("count", count));
    }

    @GetMapping("/{partId}/usage")
    public ResponseEntity<Map<String, Integer>> getTotalQuantityUsedByPart(@PathVariable Long partId) {
        Integer totalUsed = partService.getTotalQuantityUsedByPart(partId);
        return ResponseEntity.ok(Map.of("totalQuantityUsed", totalUsed));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<PartCategoryResponse>> getAllPartCategories() {
        List<PartCategoryResponse> response = partService.getAllPartCategories();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<PartCategoryResponse> getPartCategoryById(@PathVariable Long categoryId) {
        PartCategoryResponse response = partService.getPartCategoryById(categoryId);
        return ResponseEntity.ok(response);
    }
}