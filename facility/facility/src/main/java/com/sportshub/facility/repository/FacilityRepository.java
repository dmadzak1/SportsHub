package com.sportshub.facility.repository;

import com.sportshub.facility.model.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long> {

    List<Facility> findByType(String type);

    // Pretraga po dijelu naziva
    @Query("SELECT f FROM Facility f WHERE f.name LIKE %:keyword%")
    List<Facility> searchByName(@Param("keyword") String keyword);

    // Svi tereni koji imaju barem jedan raspored
    @Query("SELECT DISTINCT f FROM Facility f WHERE SIZE(f.schedules) > 0")
    List<Facility> findFacilitiesWithSchedules();
}