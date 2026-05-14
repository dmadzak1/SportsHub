package com.sportshub.facility.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportshub.facility.dto.ReservationDTO;
import com.sportshub.facility.exception.GlobalExceptionHandler;
import com.sportshub.facility.exception.ResourceNotFoundException;
import com.sportshub.facility.exception.UserServiceException;
import com.sportshub.facility.model.Facility;
import com.sportshub.facility.model.Reservation;
import com.sportshub.facility.model.ReservationStatus;
import com.sportshub.facility.model.Schedule;
import com.sportshub.facility.service.ReservationService;
import com.sportshub.facility.service.ScheduleService;
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
@Import(GlobalExceptionHandler.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private ScheduleService scheduleService;

    @Autowired
    private ObjectMapper objectMapper;

    private Reservation mockReservation() {
        Facility facility = new Facility("Teren 1", "TENNIS");
        facility.setFacilityId(1L);
        Schedule schedule = new Schedule(facility, LocalDate.now(), LocalTime.of(10, 0));
        schedule.setScheduleId(1L);
        Reservation r = new Reservation(1L, schedule, ReservationStatus.PENDING);
        r.setReservationId(1L);
        return r;
    }

    @Test
    void getAll_returnsListOfReservations() throws Exception {
        when(reservationService.getAll()).thenReturn(List.of(mockReservation()));

        mockMvc.perform(get("/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1));
    }

    @Test
    void getById_existingId_returnsReservation() throws Exception {
        when(reservationService.getById(1L)).thenReturn(mockReservation());

        mockMvc.perform(get("/reservations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));
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
        Schedule schedule = new Schedule();
        schedule.setScheduleId(1L);

        when(scheduleService.getById(1L)).thenReturn(schedule);
        when(reservationService.create(any())).thenReturn(mockReservation());

        ReservationDTO dto = new ReservationDTO();
        dto.setUserId(1L);
        dto.setScheduleId(1L);

        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void create_userServiceUnavailable_returns503() throws Exception {
        Schedule schedule = new Schedule();
        schedule.setScheduleId(1L);

        when(scheduleService.getById(1L)).thenReturn(schedule);
        when(reservationService.create(any()))
                .thenThrow(new UserServiceException("User servis nije dostupan."));

        ReservationDTO dto = new ReservationDTO();
        dto.setUserId(1L);
        dto.setScheduleId(1L);

        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("user_service_error"));
    }

    @Test
    void cancel_existingId_returnsCancelled() throws Exception {
        Reservation cancelled = mockReservation();
        cancelled.setStatus(ReservationStatus.CANCELLED);

        when(reservationService.cancelReservation(1L)).thenReturn(cancelled);

        mockMvc.perform(patch("/reservations/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void delete_existingId_returns204() throws Exception {
        mockMvc.perform(delete("/reservations/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_notFound_returns404() throws Exception {
        org.mockito.Mockito.doThrow(new ResourceNotFoundException("Reservation", 99L))
                .when(reservationService).delete(99L);

        mockMvc.perform(delete("/reservations/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not_found"));
    }
}