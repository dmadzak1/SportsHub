package com.sportshub.facility.repository;

import com.sportshub.facility.model.Reservation;
import com.sportshub.facility.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Sve rezervacije određenog korisnika
    List<Reservation> findByUserId(Long userId);

    // Sve rezervacije po statusu
    List<Reservation> findByStatus(ReservationStatus status);

    // Sve rezervacije korisnika po statusu
    List<Reservation> findByUserIdAndStatus(Long userId, ReservationStatus status);

    // Sve rezervacije za određeni termin (schedule)
    List<Reservation> findByScheduleScheduleId(Long scheduleId);
}
