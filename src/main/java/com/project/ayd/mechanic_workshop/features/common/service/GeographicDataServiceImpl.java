package com.project.ayd.mechanic_workshop.features.common.service;

import com.project.ayd.mechanic_workshop.features.common.dto.*;
import com.project.ayd.mechanic_workshop.features.common.entity.AddressDetail;
import com.project.ayd.mechanic_workshop.features.common.entity.Department;
import com.project.ayd.mechanic_workshop.features.common.entity.Municipality;
import com.project.ayd.mechanic_workshop.features.common.repository.AddressDetailRepository;
import com.project.ayd.mechanic_workshop.features.common.repository.DepartmentRepository;
import com.project.ayd.mechanic_workshop.features.common.repository.MunicipalityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeographicDataServiceImpl implements GeographicDataService {

    private final DepartmentRepository departmentRepository;
    private final MunicipalityRepository municipalityRepository;
    private final AddressDetailRepository addressDetailRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::mapToDepartmentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Department not found with ID: " + id));
        return mapToDepartmentResponse(department);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MunicipalityResponse> getAllMunicipalities() {
        return municipalityRepository.findAll().stream()
                .map(this::mapToMunicipalityResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MunicipalityResponse> getMunicipalitiesByDepartment(Long departmentId) {
        return municipalityRepository.findByDepartmentIdOrderByName(departmentId).stream()
                .map(this::mapToMunicipalityResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MunicipalityResponse getMunicipalityById(Long id) {
        Municipality municipality = municipalityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Municipality not found with ID: " + id));
        return mapToMunicipalityResponse(municipality);
    }

    @Override
    @Transactional
    public AddressDetailResponse createAddressDetail(AddressDetailRequest request) {
        Municipality municipality = municipalityRepository.findById(request.getMunicipalityId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Municipality not found with ID: " + request.getMunicipalityId()));

        AddressDetail addressDetail = new AddressDetail();
        addressDetail.setAddress(request.getAddress());
        addressDetail.setMunicipality(municipality);

        addressDetail = addressDetailRepository.save(addressDetail);
        log.info("Address detail created with ID: {}", addressDetail.getId());

        return mapToAddressDetailResponse(addressDetail);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressDetailResponse> getAddressDetailsByMunicipality(Long municipalityId) {
        return addressDetailRepository.findByMunicipalityId(municipalityId).stream()
                .map(this::mapToAddressDetailResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AddressDetailResponse getAddressDetailById(Long id) {
        AddressDetail addressDetail = addressDetailRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Address detail not found with ID: " + id));
        return mapToAddressDetailResponse(addressDetail);
    }

    private DepartmentResponse mapToDepartmentResponse(Department department) {
        return DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .createdAt(department.getCreatedAt())
                .updatedAt(department.getUpdatedAt())
                .build();
    }

    private MunicipalityResponse mapToMunicipalityResponse(Municipality municipality) {
        return MunicipalityResponse.builder()
                .id(municipality.getId())
                .name(municipality.getName())
                .department(mapToDepartmentResponse(municipality.getDepartment()))
                .createdAt(municipality.getCreatedAt())
                .updatedAt(municipality.getUpdatedAt())
                .build();
    }

    private AddressDetailResponse mapToAddressDetailResponse(AddressDetail addressDetail) {
        return AddressDetailResponse.builder()
                .id(addressDetail.getId())
                .address(addressDetail.getAddress())
                .municipality(mapToMunicipalityResponse(addressDetail.getMunicipality()))
                .createdAt(addressDetail.getCreatedAt())
                .updatedAt(addressDetail.getUpdatedAt())
                .build();
    }
}