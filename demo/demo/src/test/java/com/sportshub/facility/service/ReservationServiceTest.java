package com.sportshub.facility.service;

import com.sportshub.facility.exception.ResourceNotFoundException;
import com.sportshub.facility.model.Facility;
import com.sportshub.facility.model.Reservation;
import com.sportshub.facility.model.ReservationStatus;
import com.sportshub.facility.model.Schedule;
import com.sportshub.facility.repository.ReservationRepository;
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
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

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
    void getAll_returnsAllReservations() {
        when(reservationRepository.findAll()).thenReturn(List.of(reservation));

        List<Reservation> result = reservationService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(ReservationStatus.PENDING);
    }

    @Test
    void getById_existingId_returnsReservation() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        Reservation result = reservationService.getById(1L);

        assertThat(result.getUserId()).isEqualTo(42L);
    }

    @Test
    void getById_nonExistingId_throwsException() {
        when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getByUser_returnsFilteredReservations() {
        when(reservationRepository.findByUserId(42L)).thenReturn(List.of(reservation));

        List<Reservation> result = reservationService.getByUser(42L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(42L);
    }

    @Test
    void getByStatus_returnsFilteredReservations() {
        when(reservationRepository.findByStatus(ReservationStatus.PENDING)).thenReturn(List.of(reservation));

        List<Reservation> result = reservationService.getByStatus("PENDING");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(ReservationStatus.PENDING);
    }

    @Test
    void create_savesReservation() {
        when(reservationRepository.save(reservation)).thenReturn(reservation);

        Reservation result = reservationService.create(reservation);

        assertThat(result.getStatus()).isEqualTo(ReservationStatus.PENDING);
        verify(reservationRepository, times(1)).save(reservation);
    }

    @Test
    void updateStatus_existingId_updatesStatus() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Reservation result = reservationService.updateStatus(1L, "CONFIRMED");

        assertThat(result.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
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
