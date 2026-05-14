package com.sportshub.facility.service;

import com.sportshub.facility.exception.ResourceNotFoundException;
import com.sportshub.facility.model.Schedule;
import com.sportshub.facility.repository.ScheduleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public List<Schedule> getAll() {
        return scheduleRepository.findAll();
    }

    public Schedule getById(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", id));
    }

    public List<Schedule> getByFacility(Long facilityId) {
        return scheduleRepository.findByFacilityFacilityId(facilityId);
    }

    public List<Schedule> getByDate(LocalDate date) {
        return scheduleRepository.findByDate(date);
    }

    public List<Schedule> getByFacilityAndDate(Long facilityId, LocalDate date) {
        return scheduleRepository.findByFacilityFacilityIdAndDate(facilityId, date);
    }

    public Schedule create(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    public void delete(Long id) {
        if (!scheduleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Schedule", id);
        }
        scheduleRepository.deleteById(id);
    }

    // Paginacija
    public Page<Schedule> getPaginated(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return scheduleRepository.findAll(pageable);
    }

    // Slobodni termini
    public List<Schedule> getAvailableSchedules() {
        return scheduleRepository.findAvailableSchedules();
    }

    // Slobodni termini za određeni teren
    public List<Schedule> getAvailableSchedulesByFacility(Long facilityId) {
        return scheduleRepository.findAvailableSchedulesByFacility(facilityId);
    }
}