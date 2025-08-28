package com.project.ayd.mechanic_workshop.features.workorders.service;

import com.project.ayd.mechanic_workshop.features.workorders.dto.PartCategoryResponse;
import com.project.ayd.mechanic_workshop.features.workorders.dto.PartResponse;
import com.project.ayd.mechanic_workshop.features.workorders.entity.Part;
import com.project.ayd.mechanic_workshop.features.workorders.entity.PartCategory;
import com.project.ayd.mechanic_workshop.features.workorders.repository.PartCategoryRepository;
import com.project.ayd.mechanic_workshop.features.workorders.repository.PartRepository;
import com.project.ayd.mechanic_workshop.features.workorders.repository.WorkPartRepository;
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
public class PartServiceImpl implements PartService {

    private final PartRepository partRepository;
    private final PartCategoryRepository partCategoryRepository;
    private final WorkPartRepository workPartRepository;

    @Override
    @Transactional(readOnly = true)
    public PartResponse getPartById(Long partId) {
        Part part = partRepository.findByIdWithCategory(partId)
                .orElseThrow(() -> new IllegalArgumentException("Part not found with ID: " + partId));
        return mapToPartResponse(part);
    }

    @Override
    @Transactional(readOnly = true)
    public PartResponse getPartByPartNumber(String partNumber) {
        Part part = partRepository.findByPartNumber(partNumber)
                .orElseThrow(() -> new IllegalArgumentException("Part not found with part number: " + partNumber));
        return mapToPartResponse(part);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartResponse> getAllActiveParts() {
        return partRepository.findAllActiveOrderByNameAsc()
                .stream()
                .map(this::mapToPartResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PartResponse> getAllActivePartsPageable(Pageable pageable) {
        return partRepository.findByIsActiveTrueOrderByNameAsc(pageable)
                .map(this::mapToPartResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartResponse> getPartsByCategory(Long categoryId) {
        return partRepository.findByCategoryIdAndIsActiveTrueOrderByNameAsc(categoryId)
                .stream()
                .map(this::mapToPartResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PartResponse> searchPartsByName(String name, Pageable pageable) {
        return partRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(name, pageable)
                .map(this::mapToPartResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPartNumberExists(String partNumber) {
        return partRepository.existsByPartNumber(partNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartCategoryResponse> getAllPartCategories() {
        return partCategoryRepository.findAll()
                .stream()
                .map(this::mapToPartCategoryResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PartCategoryResponse getPartCategoryById(Long categoryId) {
        PartCategory category = partCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Part category not found with ID: " + categoryId));
        return mapToPartCategoryResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countActiveParts() {
        return partRepository.countActiveparts();
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getTotalQuantityUsedByPart(Long partId) {
        return workPartRepository.sumQuantityUsedByPartId(partId);
    }

    private PartResponse mapToPartResponse(Part part) {
        PartCategoryResponse categoryResponse = part.getCategory() != null
                ? mapToPartCategoryResponse(part.getCategory())
                : null;

        return PartResponse.builder()
                .id(part.getId())
                .name(part.getName())
                .description(part.getDescription())
                .category(categoryResponse)
                .unitPrice(part.getUnitPrice())
                .minimumStock(part.getMinimumStock())
                .createdAt(part.getCreatedAt())
                .updatedAt(part.getUpdatedAt())
                .build();
    }

    private PartCategoryResponse mapToPartCategoryResponse(PartCategory category) {
        return PartCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .build();
    }
}