package com.sportshub.facility.controller;

import com.sportshub.facility.dto.PageResponseDTO;
import com.sportshub.facility.dto.ReservationBatchDTO;
import com.sportshub.facility.dto.ReservationDTO;
import com.sportshub.facility.model.Reservation;
import com.sportshub.facility.model.ReservationStatus;
import com.sportshub.facility.model.Schedule;
import com.sportshub.facility.service.ReservationService;
import com.sportshub.facility.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final ScheduleService scheduleService;

    public ReservationController(ReservationService reservationService,
                                 ScheduleService scheduleService) {
        this.reservationService = reservationService;
        this.scheduleService = scheduleService;
    }

    @GetMapping
    public List<ReservationDTO> getAll() {
        return reservationService.getAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @GetMapping("/paginated")
    public PageResponseDTO<ReservationDTO> getPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "reservationId") String sortBy) {

        Page<Reservation> result = reservationService.getPaginated(page, size, sortBy);
        List<ReservationDTO> content = result.getContent().stream()
                .map(this::toDTO)
                .toList();
        return new PageResponseDTO<>(content, page, size,
                result.getTotalElements(), result.getTotalPages());
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

    @GetMapping("/date-range")
    public List<ReservationDTO> getByDateRange(@RequestParam String from,
                                               @RequestParam String to) {
        return reservationService.getByDateRange(
                        LocalDate.parse(from), LocalDate.parse(to)).stream()
                .map(this::toDTO)
                .toList();
    }

    @GetMapping("/user/{userId}/count")
    public Long countByUser(@PathVariable Long userId) {
        return reservationService.countByUser(userId);
    }

    @PostMapping
    public ResponseEntity<ReservationDTO> create(@Valid @RequestBody ReservationDTO dto) {
        Schedule schedule = scheduleService.getById(dto.getScheduleId());
        Reservation reservation = new Reservation(dto.getUserId(), schedule,
                ReservationStatus.PENDING);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toDTO(reservationService.create(reservation)));
    }

    @PostMapping("/batch")
    public ResponseEntity<List<ReservationDTO>> createBatch(
            @Valid @RequestBody ReservationBatchDTO batchDTO) {

        List<Reservation> reservations = batchDTO.getReservations().stream()
                .map(dto -> {
                    Schedule schedule = scheduleService.getById(dto.getScheduleId());
                    return new Reservation(dto.getUserId(), schedule, ReservationStatus.PENDING);
                })
                .toList();

        List<ReservationDTO> created = reservationService.createBatch(reservations).stream()
                .map(this::toDTO)
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}/status")
    public ReservationDTO updateStatus(@PathVariable Long id, @RequestParam String status) {
        return toDTO(reservationService.updateStatus(id, status));
    }

    @PatchMapping("/{id}/cancel")
    public ReservationDTO cancel(@PathVariable Long id) {
        return toDTO(reservationService.cancelReservation(id));
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