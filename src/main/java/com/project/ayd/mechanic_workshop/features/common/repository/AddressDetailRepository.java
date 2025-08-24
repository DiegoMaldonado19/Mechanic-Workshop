package com.project.ayd.mechanic_workshop.features.common.repository;

import com.project.ayd.mechanic_workshop.features.common.entity.AddressDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressDetailRepository extends JpaRepository<AddressDetail, Long> {

    List<AddressDetail> findByMunicipalityId(Long municipalityId);

    @Query("SELECT a FROM AddressDetail a WHERE a.municipality.department.id = :departmentId")
    List<AddressDetail> findByDepartmentId(@Param("departmentId") Long departmentId);

    @Query("SELECT a FROM AddressDetail a WHERE a.address LIKE %:address%")
    List<AddressDetail> findByAddressContaining(@Param("address") String address);
}