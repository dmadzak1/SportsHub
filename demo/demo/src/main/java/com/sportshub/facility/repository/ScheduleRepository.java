package com.sportshub.facility.repository;

import com.sportshub.facility.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // Svi termini za određeni teren
    List<Schedule> findByFacilityFacilityId(Long facilityId);

    // Svi termini za određeni datum
    List<Schedule> findByDate(LocalDate date);

    // Svi termini za određeni teren na određeni datum
    List<Schedule> findByFacilityFacilityIdAndDate(Long facilityId, LocalDate date);
}