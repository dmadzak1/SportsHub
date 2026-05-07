package com.sportshub.facility.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportshub.facility.dto.ReservationDTO;
import com.sportshub.facility.exception.GlobalExceptionHandler;
import com.sportshub.facility.exception.ResourceNotFoundException;
import com.sportshub.facility.model.Facility;
import com.sportshub.facility.model.Reservation;
import com.sportshub.facility.model.ReservationStatus;
import com.sportshub.facility.model.Schedule;
import com.sportshub.facility.service.ReservationService;
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

@WebMvcTest(ReservationController.class)
@Import({GlobalExceptionHandler.class})
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private ScheduleService scheduleService;

    @Autowired
    private ObjectMapper objectMapper;

    private Schedule schedule;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        Facility facility = new Facility("Teren 1", "TENNIS");
        facility.setFacilityId(1L);
        schedule = new Schedule(facility, LocalDate.of(2025, 6, 1), LocalTime.of(10, 0));
        schedule.setScheduleId(1L);
        reservation = new Reservation(42L, schedule, ReservationStatus.PENDING);
        reservation.setReservationId(1L);
    }

    @Test
    void getAll_returnsListOfReservations() throws Exception {
        when(reservationService.getAll()).thenReturn(List.of(reservation));

        mockMvc.perform(get("/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(42))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    void getById_existingId_returnsReservation() throws Exception {
        when(reservationService.getById(1L)).thenReturn(reservation);

        mockMvc.perform(get("/reservations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(reservationService.getById(99L))
                .thenThrow(new ResourceNotFoundException("Reservation", 99L));

        mockMvc.perform(get("/reservations/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not_found"));
    }

    @Test
    void create_validRequest_returns201() throws Exception {
        ReservationDTO dto = new ReservationDTO();
        dto.setUserId(42L);
        dto.setScheduleId(1L);

        when(scheduleService.getById(1L)).thenReturn(schedule);
        when(reservationService.create(any())).thenReturn(reservation);

        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(42));
    }

    @Test
    void create_invalidRequest_returns400() throws Exception {
        ReservationDTO dto = new ReservationDTO();
        // userId and scheduleId are null

        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation"));
    }

    @Test
    void updateStatus_existingId_returnsUpdatedReservation() throws Exception {
        Reservation confirmed = new Reservation(42L, schedule, ReservationStatus.CONFIRMED);
        confirmed.setReservationId(1L);
        when(reservationService.updateStatus(1L, "CONFIRMED")).thenReturn(confirmed);

        mockMvc.perform(patch("/reservations/1/status")
                        .param("status", "CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void delete_existingId_returns204() throws Exception {
        mockMvc.perform(delete("/reservations/1"))
                .andExpect(status().isNoContent());
    }
}
