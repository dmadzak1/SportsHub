package com.sportshub.facility.service;

import com.sportshub.facility.exception.ResourceNotFoundException;
import com.sportshub.facility.model.Facility;
import com.sportshub.facility.model.Schedule;
import com.sportshub.facility.repository.ScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @InjectMocks
    private ScheduleService scheduleService;

    private Facility facility;
    private Schedule schedule;

    @BeforeEach
    void setUp() {
        facility = new Facility("Teren 1", "TENNIS");
        facility.setFacilityId(1L);
        schedule = new Schedule(facility, LocalDate.of(2025, 6, 1), LocalTime.of(10, 0));
        schedule.setScheduleId(1L);
    }

    @Test
    void getAll_returnsAllSchedules() {
        when(scheduleRepository.findAll()).thenReturn(List.of(schedule));

        List<Schedule> result = scheduleService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTimeSlot()).isEqualTo(LocalTime.of(10, 0));
    }

    @Test
    void getById_existingId_returnsSchedule() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));

        Schedule result = scheduleService.getById(1L);

        assertThat(result.getDate()).isEqualTo(LocalDate.of(2025, 6, 1));
    }

    @Test
    void getById_nonExistingId_throwsException() {
        when(scheduleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> scheduleService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getByFacility_returnsFilteredSchedules() {
        when(scheduleRepository.findByFacilityFacilityId(1L)).thenReturn(List.of(schedule));

        List<Schedule> result = scheduleService.getByFacility(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void getByDate_returnsFilteredSchedules() {
        LocalDate date = LocalDate.of(2025, 6, 1);
        when(scheduleRepository.findByDate(date)).thenReturn(List.of(schedule));

        List<Schedule> result = scheduleService.getByDate(date);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDate()).isEqualTo(date);
    }

    @Test
    void create_savesSchedule() {
        when(scheduleRepository.save(schedule)).thenReturn(schedule);

        Schedule result = scheduleService.create(schedule);

        assertThat(result.getTimeSlot()).isEqualTo(LocalTime.of(10, 0));
        verify(scheduleRepository, times(1)).save(schedule);
    }

    @Test
    void delete_existingId_deletesSuccessfully() {
        when(scheduleRepository.existsById(1L)).thenReturn(true);

        scheduleService.delete(1L);

        verify(scheduleRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(scheduleRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> scheduleService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
