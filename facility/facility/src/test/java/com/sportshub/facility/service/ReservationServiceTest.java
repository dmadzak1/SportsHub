package com.sportshub.facility.service;

import com.sportshub.facility.client.UserServiceClient;
import com.sportshub.facility.dto.UserResponseDTO;
import com.sportshub.facility.exception.ResourceNotFoundException;
import com.sportshub.facility.exception.UserServiceException;
import com.sportshub.facility.model.Facility;
import com.sportshub.facility.model.Reservation;
import com.sportshub.facility.model.ReservationStatus;
import com.sportshub.facility.model.Schedule;
import com.sportshub.facility.repository.ReservationRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private ReservationService reservationService;

    private Reservation reservation;
    private UserResponseDTO userResponseDTO;

    @BeforeEach
    void setUp() {
        Facility facility = new Facility("Teren 1", "TENNIS");
        facility.setFacilityId(1L);

        Schedule schedule = new Schedule(facility, LocalDate.now(), LocalTime.of(10, 0));
        schedule.setScheduleId(1L);

        reservation = new Reservation(1L, schedule, ReservationStatus.PENDING);
        reservation.setReservationId(1L);

        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setUserId(1L);
        userResponseDTO.setEmail("ana@email.com");
    }

    @Test
    void create_validUser_createsReservation() {
        when(userServiceClient.getUserById(1L)).thenReturn(userResponseDTO);
        when(reservationRepository.save(any())).thenReturn(reservation);

        Reservation result = reservationService.create(reservation);

        assertThat(result.getUserId()).isEqualTo(1L);
        verify(reservationRepository, times(1)).save(reservation);
    }

    @Test
    void create_userServiceUnavailable_throwsException() {
        when(userServiceClient.getUserById(1L)).thenReturn(null);

        assertThatThrownBy(() -> reservationService.create(reservation))
                .isInstanceOf(UserServiceException.class)
                .hasMessageContaining("User servis nije dostupan");
    }

    @Test
    void create_userNotFound_throwsException() {
        UserResponseDTO emptyUser = new UserResponseDTO();
        when(userServiceClient.getUserById(1L)).thenReturn(emptyUser);

        assertThatThrownBy(() -> reservationService.create(reservation))
                .isInstanceOf(UserServiceException.class)
                .hasMessageContaining("ne postoji");
    }

    @Test
    void createBatch_validUsers_createsAll() {
        when(userServiceClient.getUserById(1L)).thenReturn(userResponseDTO);
        when(reservationRepository.saveAll(any())).thenReturn(List.of(reservation));

        List<Reservation> result = reservationService.createBatch(List.of(reservation));

        assertThat(result).hasSize(1);
    }

    @Test
    void createBatch_userServiceUnavailable_throwsException() {
        when(userServiceClient.getUserById(1L)).thenReturn(null);

        assertThatThrownBy(() -> reservationService.createBatch(List.of(reservation)))
                .isInstanceOf(UserServiceException.class);
    }

    @Test
    void getById_existingId_returnsReservation() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        Reservation result = reservationService.getById(1L);

        assertThat(result.getUserId()).isEqualTo(1L);
    }

    @Test
    void getById_nonExistingId_throwsException() {
        when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void cancelReservation_existingId_cancels() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any())).thenReturn(reservation);

        Reservation result = reservationService.cancelReservation(1L);

        assertThat(result.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
    }

    @Test
    void delete_existingId_deletesSuccessfully() {
        when(reservationRepository.existsById(1L)).thenReturn(true);

        reservationService.delete(1L);

        verify(reservationRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(reservationRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> reservationService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}