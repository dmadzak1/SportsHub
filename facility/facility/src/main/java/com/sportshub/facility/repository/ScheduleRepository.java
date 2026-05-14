package com.sportshub.facility.repository;

import com.sportshub.facility.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByFacilityFacilityId(Long facilityId);
    List<Schedule> findByDate(LocalDate date);
    List<Schedule> findByFacilityFacilityIdAndDate(Long facilityId, LocalDate date);

    // Slobodni termini — nemaju rezervaciju
    @Query("SELECT s FROM Schedule s WHERE s NOT IN " +
            "(SELECT r.schedule FROM Reservation r WHERE r.status = 'CONFIRMED')")
    List<Schedule> findAvailableSchedules();

    // Slobodni termini za određeni teren
    @Query("SELECT s FROM Schedule s WHERE s.facility.facilityId = :facilityId AND s NOT IN " +
            "(SELECT r.schedule FROM Reservation r WHERE r.status = 'CONFIRMED')")
    List<Schedule> findAvailableSchedulesByFacility(@Param("facilityId") Long facilityId);
}