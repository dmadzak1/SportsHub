package com.sportshub.facility.controller;

import com.sportshub.facility.model.Reservation;
import com.sportshub.facility.model.ReservationStatus;
import com.sportshub.facility.repository.ReservationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationRepository reservationRepository;

    public ReservationController(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    // GET /reservations
    @GetMapping
    public List<Reservation> getAll() {
        return reservationRepository.findAll();
    }

    // GET /reservations/1
    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getById(@PathVariable Long id) {
        return reservationRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /reservations/user/1
    @GetMapping("/user/{userId}")
    public List<Reservation> getByUser(@PathVariable Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    // GET /reservations/status/CONFIRMED
    @GetMapping("/status/{status}")
    public List<Reservation> getByStatus(@PathVariable String status) {
        return reservationRepository.findByStatus(ReservationStatus.valueOf(status.toUpperCase()));
    }

    // GET /reservations/user/1/status/CONFIRMED
    @GetMapping("/user/{userId}/status/{status}")
    public List<Reservation> getByUserAndStatus(@PathVariable Long userId,
                                                @PathVariable String status) {
        return reservationRepository.findByUserIdAndStatus(userId, ReservationStatus.valueOf(status.toUpperCase()));
    }

    // POST /reservations
    @PostMapping
    public Reservation create(@RequestBody Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    // DELETE /reservations/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!reservationRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        reservationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}