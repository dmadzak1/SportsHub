package com.sportshub.facility.controller;

import com.sportshub.facility.dto.ScheduleDTO;
import com.sportshub.facility.model.Facility;
import com.sportshub.facility.model.Schedule;
import com.sportshub.facility.service.FacilityService;
import com.sportshub.facility.service.ScheduleService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

    public ScheduleController(ScheduleService scheduleService,
                              FacilityService facilityService,
                              ModelMapper modelMapper) {
        this.scheduleService = scheduleService;
        this.facilityService = facilityService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<ScheduleDTO> getAll() {
        return scheduleService.getAll().stream()
                .map(this::toDTO)
                .toList();
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

    // Helper: Schedule -> ScheduleDTO (jer facilityId nije direktno polje)
    private ScheduleDTO toDTO(Schedule schedule) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setScheduleId(schedule.getScheduleId());
        dto.setFacilityId(schedule.getFacility().getFacilityId());
        dto.setDate(schedule.getDate());
        dto.setTimeSlot(schedule.getTimeSlot());
        return dto;
    }
}