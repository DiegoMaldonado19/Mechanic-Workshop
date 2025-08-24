package com.project.ayd.mechanic_workshop.features.common.service;

import com.project.ayd.mechanic_workshop.features.common.dto.*;

import java.util.List;

public interface GeographicDataService {

    List<DepartmentResponse> getAllDepartments();

    DepartmentResponse getDepartmentById(Long id);

    List<MunicipalityResponse> getAllMunicipalities();

    List<MunicipalityResponse> getMunicipalitiesByDepartment(Long departmentId);

    MunicipalityResponse getMunicipalityById(Long id);

    AddressDetailResponse createAddressDetail(AddressDetailRequest request);

    List<AddressDetailResponse> getAddressDetailsByMunicipality(Long municipalityId);

    AddressDetailResponse getAddressDetailById(Long id);
}