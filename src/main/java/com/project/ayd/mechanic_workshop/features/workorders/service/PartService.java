package com.project.ayd.mechanic_workshop.features.workorders.service;

import com.project.ayd.mechanic_workshop.features.workorders.dto.PartCategoryResponse;
import com.project.ayd.mechanic_workshop.features.workorders.dto.PartResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PartService {

    PartResponse getPartById(Long partId);

    PartResponse getPartByPartNumber(String partNumber);

    List<PartResponse> getAllActiveParts();

    Page<PartResponse> getAllActivePartsPageable(Pageable pageable);

    List<PartResponse> getPartsByCategory(Long categoryId);

    Page<PartResponse> searchPartsByName(String name, Pageable pageable);

    boolean isPartNumberExists(String partNumber);

    List<PartCategoryResponse> getAllPartCategories();

    PartCategoryResponse getPartCategoryById(Long categoryId);

    Long countActiveParts();

    Integer getTotalQuantityUsedByPart(Long partId);
}