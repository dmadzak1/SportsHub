package com.sportshub.facility.service;

import com.sportshub.facility.exception.ResourceNotFoundException;
import com.sportshub.facility.model.Reservation;
import com.sportshub.facility.model.ReservationStatus;
import com.sportshub.facility.repository.ReservationRepository;
import com.sportshub.facility.repository.ScheduleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ScheduleRepository scheduleRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              ScheduleRepository scheduleRepository) {
        this.reservationRepository = reservationRepository;
        this.scheduleRepository = scheduleRepository;
    }

    public List<Reservation> getAll() {
        return reservationRepository.findAll();
    }

    public Reservation getById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));
    }

    public List<Reservation> getByUser(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    public List<Reservation> getByStatus(String status) {
        return reservationRepository.findByStatus(
                ReservationStatus.valueOf(status.toUpperCase()));
    }

    public List<Reservation> getByUserAndStatus(Long userId, String status) {
        return reservationRepository.findByUserIdAndStatus(
                userId, ReservationStatus.valueOf(status.toUpperCase()));
    }

    public Reservation create(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public Reservation updateStatus(Long id, String status) {
        Reservation existing = getById(id);
        existing.setStatus(ReservationStatus.valueOf(status.toUpperCase()));
        return reservationRepository.save(existing);
    }

    public void delete(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reservation", id);
        }
        reservationRepository.deleteById(id);
    }

    // Paginacija
    public Page<Reservation> getPaginated(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return reservationRepository.findAll(pageable);
    }

    // Rezervacije u datumskom rasponu
    public List<Reservation> getByDateRange(LocalDate from, LocalDate to) {
        return reservationRepository.findByDateRange(from, to);
    }

    // Broj rezervacija korisnika
    public Long countByUser(Long userId) {
        return reservationRepository.countByUserId(userId);
    }

    // Transakcijska metoda — otkazivanje rezervacije oslobađa termin
    @Transactional
    public Reservation cancelReservation(Long id) {
        Reservation reservation = getById(id);
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
        return reservation;
    }

    // Batch unos
    @Transactional
    public List<Reservation> createBatch(List<Reservation> reservations) {
        return reservationRepository.saveAll(reservations);
    }
}