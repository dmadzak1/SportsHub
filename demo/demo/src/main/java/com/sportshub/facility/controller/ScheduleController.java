package com.sportshub.facility.controller;

import com.sportshub.facility.model.Schedule;
import com.sportshub.facility.repository.ScheduleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleRepository scheduleRepository;

    public ScheduleController(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    // GET /schedules
    @GetMapping
    public List<Schedule> getAll() {
        return scheduleRepository.findAll();
    }

    // GET /schedules/1
    @GetMapping("/{id}")
    public ResponseEntity<Schedule> getById(@PathVariable Long id) {
        return scheduleRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /schedules/facility/1
    @GetMapping("/facility/{facilityId}")
    public List<Schedule> getByFacility(@PathVariable Long facilityId) {
        return scheduleRepository.findByFacilityFacilityId(facilityId);
    }

    // GET /schedules/date/2026-05-06
    @GetMapping("/date/{date}")
    public List<Schedule> getByDate(@PathVariable String date) {
        return scheduleRepository.findByDate(LocalDate.parse(date));
    }

    // GET /schedules/facility/1/date/2026-05-06
    @GetMapping("/facility/{facilityId}/date/{date}")
    public List<Schedule> getByFacilityAndDate(@PathVariable Long facilityId,
                                               @PathVariable String date) {
        return scheduleRepository.findByFacilityFacilityIdAndDate(facilityId, LocalDate.parse(date));
    }

    // POST /schedules
    @PostMapping
    public Schedule create(@RequestBody Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    // DELETE /schedules/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!scheduleRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        scheduleRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
