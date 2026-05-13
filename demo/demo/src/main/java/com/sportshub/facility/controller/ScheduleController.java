package com.sportshub.facility.controller;

import com.sportshub.facility.dto.PageResponseDTO;
import com.sportshub.facility.dto.ScheduleDTO;
import com.sportshub.facility.model.Facility;
import com.sportshub.facility.model.Schedule;
import com.sportshub.facility.service.FacilityService;
import com.sportshub.facility.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final FacilityService facilityService;

    public ScheduleController(ScheduleService scheduleService,
                              FacilityService facilityService) {
        this.scheduleService = scheduleService;
        this.facilityService = facilityService;
    }

    @GetMapping
    public List<ScheduleDTO> getAll() {
        return scheduleService.getAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @GetMapping("/paginated")
    public PageResponseDTO<ScheduleDTO> getPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "scheduleId") String sortBy) {

        Page<Schedule> result = scheduleService.getPaginated(page, size, sortBy);
        List<ScheduleDTO> content = result.getContent().stream()
                .map(this::toDTO)
                .toList();
        return new PageResponseDTO<>(content, page, size,
                result.getTotalElements(), result.getTotalPages());
    }

    @GetMapping("/{id}")
    public ScheduleDTO getById(@PathVariable Long id) {
        return toDTO(scheduleService.getById(id));
    }

    @GetMapping("/facility/{facilityId}")
    public List<ScheduleDTO> getByFacility(@PathVariable Long facilityId) {
        return scheduleService.getByFacility(facilityId).stream()
                .map(this::toDTO)
                .toList();
    }

    @GetMapping("/date/{date}")
    public List<ScheduleDTO> getByDate(@PathVariable String date) {
        return scheduleService.getByDate(LocalDate.parse(date)).stream()
                .map(this::toDTO)
                .toList();
    }

    @GetMapping("/facility/{facilityId}/date/{date}")
    public List<ScheduleDTO> getByFacilityAndDate(@PathVariable Long facilityId,
                                                  @PathVariable String date) {
        return scheduleService.getByFacilityAndDate(facilityId, LocalDate.parse(date)).stream()
                .map(this::toDTO)
                .toList();
    }

    @GetMapping("/available")
    public List<ScheduleDTO> getAvailable() {
        return scheduleService.getAvailableSchedules().stream()
                .map(this::toDTO)
                .toList();
    }

    @GetMapping("/available/facility/{facilityId}")
    public List<ScheduleDTO> getAvailableByFacility(@PathVariable Long facilityId) {
        return scheduleService.getAvailableSchedulesByFacility(facilityId).stream()
                .map(this::toDTO)
                .toList();
    }

    @PostMapping
    public ResponseEntity<ScheduleDTO> create(@Valid @RequestBody ScheduleDTO dto) {
        Facility facility = facilityService.getById(dto.getFacilityId());
        Schedule schedule = new Schedule(facility, dto.getDate(), dto.getTimeSlot());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toDTO(scheduleService.create(schedule)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        scheduleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private ScheduleDTO toDTO(Schedule schedule) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setScheduleId(schedule.getScheduleId());
        dto.setFacilityId(schedule.getFacility().getFacilityId());
        dto.setDate(schedule.getDate());
        dto.setTimeSlot(schedule.getTimeSlot());
        return dto;
    }
}