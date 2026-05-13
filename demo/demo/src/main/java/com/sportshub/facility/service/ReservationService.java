package com.sportshub.facility.service;

import com.sportshub.facility.client.UserServiceClient;
import com.sportshub.facility.dto.UserResponseDTO;
import com.sportshub.facility.exception.ResourceNotFoundException;
import com.sportshub.facility.exception.UserServiceException;
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
    private final UserServiceClient userServiceClient;

    public ReservationService(ReservationRepository reservationRepository,
                              ScheduleRepository scheduleRepository,
                              UserServiceClient userServiceClient) {
        this.reservationRepository = reservationRepository;
        this.scheduleRepository = scheduleRepository;
        this.userServiceClient = userServiceClient;
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

    public List<Reservation> getByDateRange(LocalDate from, LocalDate to) {
        return reservationRepository.findByDateRange(from, to);
    }

    public Long countByUser(Long userId) {
        return reservationRepository.countByUserId(userId);
    }

    public Page<Reservation> getPaginated(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return reservationRepository.findAll(pageable);
    }

    // Kreiranje rezervacije sa validacijom korisnika
    @Transactional
    public Reservation create(Reservation reservation) {
        // Provjeri da li korisnik postoji u User servisu
        UserResponseDTO user = userServiceClient.getUserById(reservation.getUserId());

        if (user == null) {
            throw new UserServiceException(
                    "User servis nije dostupan. Pokušajte ponovo kasnije.");
        }

        if (user.getUserId() == null) {
            throw new UserServiceException(
                    "Korisnik sa ID-om " + reservation.getUserId() + " ne postoji.");
        }

        return reservationRepository.save(reservation);
    }

    public Reservation updateStatus(Long id, String status) {
        Reservation existing = getById(id);
        existing.setStatus(ReservationStatus.valueOf(status.toUpperCase()));
        return reservationRepository.save(existing);
    }

    @Transactional
    public Reservation cancelReservation(Long id) {
        Reservation reservation = getById(id);
        reservation.setStatus(ReservationStatus.CANCELLED);
        return reservationRepository.save(reservation);
    }

    @Transactional
    public List<Reservation> createBatch(List<Reservation> reservations) {
        // Validiraj sve korisnike prije batch unosa
        for (Reservation reservation : reservations) {
            UserResponseDTO user = userServiceClient.getUserById(reservation.getUserId());
            if (user == null) {
                throw new UserServiceException(
                        "User servis nije dostupan. Pokušajte ponovo kasnije.");
            }
            if (user.getUserId() == null) {
                throw new UserServiceException(
                        "Korisnik sa ID-om " + reservation.getUserId() + " ne postoji.");
            }
        }
        return reservationRepository.saveAll(reservations);
    }

    public void delete(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reservation", id);
        }
        reservationRepository.deleteById(id);
    }
}