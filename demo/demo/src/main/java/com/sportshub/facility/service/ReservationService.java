package com.sportshub.facility.service;

import com.sportshub.facility.model.Reservation;
import com.sportshub.facility.model.ReservationStatus;
import com.sportshub.facility.repository.ReservationRepository;
import com.sportshub.facility.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
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
        return reservationRepository.findByStatus(ReservationStatus.valueOf(status.toUpperCase()));
    }

    public List<Reservation> getByUserAndStatus(Long userId, String status) {
        return reservationRepository.findByUserIdAndStatus(userId, ReservationStatus.valueOf(status.toUpperCase()));
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
}