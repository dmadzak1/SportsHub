package com.sportshub.facility.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportshub.facility.dto.ScheduleDTO;
import com.sportshub.facility.exception.GlobalExceptionHandler;
import com.sportshub.facility.exception.ResourceNotFoundException;
import com.sportshub.facility.model.Facility;
import com.sportshub.facility.model.Schedule;
import com.sportshub.facility.service.FacilityService;
import com.sportshub.facility.service.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ScheduleController.class)
@Import({GlobalExceptionHandler.class})
class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScheduleService scheduleService;

    @MockBean
    private FacilityService facilityService;

    @Autowired
    private ObjectMapper objectMapper;

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
    void getAll_returnsListOfSchedules() throws Exception {
        when(scheduleService.getAll()).thenReturn(List.of(schedule));

        mockMvc.perform(get("/schedules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].facilityId").value(1))
                .andExpect(jsonPath("$[0].date").value("2025-06-01"));
    }

    @Test
    void getById_existingId_returnsSchedule() throws Exception {
        when(scheduleService.getById(1L)).thenReturn(schedule);

        mockMvc.perform(get("/schedules/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2025-06-01"));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(scheduleService.getById(99L))
                .thenThrow(new ResourceNotFoundException("Schedule", 99L));

        mockMvc.perform(get("/schedules/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not_found"));
    }

    @Test
    void create_validRequest_returns201() throws Exception {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setFacilityId(1L);
        dto.setDate(LocalDate.of(2025, 6, 1));
        dto.setTimeSlot(LocalTime.of(10, 0));

        when(facilityService.getById(1L)).thenReturn(facility);
        when(scheduleService.create(any())).thenReturn(schedule);

        mockMvc.perform(post("/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.facilityId").value(1));
    }

    @Test
    void create_invalidRequest_returns400() throws Exception {
        ScheduleDTO dto = new ScheduleDTO();
        // facilityId, date, and timeSlot are null

        mockMvc.perform(post("/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation"));
    }

    @Test
    void delete_existingId_returns204() throws Exception {
        mockMvc.perform(delete("/schedules/1"))
                .andExpect(status().isNoContent());
    }
}
