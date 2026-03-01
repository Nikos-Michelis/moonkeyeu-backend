package com.moonkeyeu.core.api.launch.repository;

import com.moonkeyeu.core.api.launch.model.agency.Agencies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgenciesRepository extends JpaRepository<Agencies, Long>, JpaSpecificationExecutor<Agencies> {
    @Query("""
            SELECT a FROM Agencies a
            LEFT JOIN FETCH a.agencyType
            LEFT JOIN FETCH a.rocketConfigurations
            LEFT JOIN FETCH a.spacecraftConfigurations sp
            LEFT JOIN FETCH sp.spacecraftType
            LEFT JOIN FETCH sp.spacecraftConfImages
            LEFT JOIN FETCH a.countries
            WHERE a.agencyId = :agencyId
    """)
    Optional<Agencies> findAgencyById(@Param("agencyId") Integer agencyId);

    @Query("""
            SELECT a FROM Agencies a
            LEFT JOIN FETCH a.agencyType
            LEFT JOIN FETCH a.rocketConfigurations
            LEFT JOIN FETCH a.spacecraftConfigurations
            LEFT JOIN FETCH a.countries
            WHERE a.featured IS TRUE
            ORDER BY a.agencyName ASC
    """)
    List<Agencies> findAll();
}
