package com.sportshub.facility.repository;

import com.sportshub.facility.model.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long> {

    // Pronađi sve terence/bazene po tipu (npr. "TENNIS")
    List<Facility> findByType(String type);
}
