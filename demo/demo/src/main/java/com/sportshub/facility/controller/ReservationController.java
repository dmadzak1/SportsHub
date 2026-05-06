package com.sportshub.facility.controller;

import com.sportshub.facility.dto.ReservationDTO;
import com.sportshub.facility.model.Reservation;
import com.sportshub.facility.model.ReservationStatus;
import com.sportshub.facility.model.Schedule;
import com.sportshub.facility.service.ReservationService;
import com.sportshub.facility.service.ScheduleService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final ScheduleService scheduleService;
    private final ModelMapper modelMapper;

    public ReservationController(ReservationService reservationService,
                                 ScheduleService scheduleService,
                                 ModelMapper modelMapper) {
        this.reservationService = reservationService;
        this.scheduleService = scheduleService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<ReservationDTO> getAll() {
        return reservationService.getAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ReservationDTO getById(@PathVariable Long id) {
        return toDTO(reservationService.getById(id));
    }

    @GetMapping("/user/{userId}")
    public List<ReservationDTO> getByUser(@PathVariable Long userId) {
        return reservationService.getByUser(userId).stream()
                .map(this::toDTO)
                .toList();
    }

    @GetMapping("/status/{status}")
    public List<ReservationDTO> getByStatus(@PathVariable String status) {
        return reservationService.getByStatus(status).stream()
                .map(this::toDTO)
                .toList();
    }

    @GetMapping("/user/{userId}/status/{status}")
    public List<ReservationDTO> getByUserAndStatus(@PathVariable Long userId,
                                                   @PathVariable String status) {
        return reservationService.getByUserAndStatus(userId, status).stream()
                .map(this::toDTO)
                .toList();
    }

    @PostMapping
    public ResponseEntity<ReservationDTO> create(@Valid @RequestBody ReservationDTO dto) {
        Schedule schedule = scheduleService.getById(dto.getScheduleId());
        Reservation reservation = new Reservation(
                dto.getUserId(),
                schedule,
                ReservationStatus.PENDING
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toDTO(reservationService.create(reservation)));
    }

    @PatchMapping("/{id}/status")
    public ReservationDTO updateStatus(@PathVariable Long id, @RequestParam String status) {
        return toDTO(reservationService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private ReservationDTO toDTO(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setReservationId(reservation.getReservationId());
        dto.setUserId(reservation.getUserId());
        dto.setScheduleId(reservation.getSchedule().getScheduleId());
        dto.setStatus(reservation.getStatus().name());
        return dto;
    }
}