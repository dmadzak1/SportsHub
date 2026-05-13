package com.sportshub.facility.repository;

import com.sportshub.facility.model.Reservation;
import com.sportshub.facility.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserId(Long userId);
    List<Reservation> findByStatus(ReservationStatus status);
    List<Reservation> findByUserIdAndStatus(Long userId, ReservationStatus status);
    List<Reservation> findByScheduleScheduleId(Long scheduleId);

    // Rezervacije u određenom datumskom rasponu
    @Query("SELECT r FROM Reservation r WHERE r.schedule.date BETWEEN :from AND :to")
    List<Reservation> findByDateRange(@Param("from") LocalDate from, @Param("to") LocalDate to);

    // Broj rezervacija po korisniku
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);
}