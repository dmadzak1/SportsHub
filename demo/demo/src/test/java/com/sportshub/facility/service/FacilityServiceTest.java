package com.sportshub.facility.service;

import com.sportshub.facility.exception.ResourceNotFoundException;
import com.sportshub.facility.model.Facility;
import com.sportshub.facility.repository.FacilityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FacilityServiceTest {

    @Mock
    private FacilityRepository facilityRepository;

    @InjectMocks
    private FacilityService facilityService;

    private Facility facility;

    @BeforeEach
    void setUp() {
        facility = new Facility("Teren 1", "TENNIS");
        facility.setFacilityId(1L);
    }

    @Test
    void getAll_returnsAllFacilities() {
        when(facilityRepository.findAll()).thenReturn(List.of(facility));

        List<Facility> result = facilityService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Teren 1");
    }

    @Test
    void getById_existingId_returnsFacility() {
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));

        Facility result = facilityService.getById(1L);

        assertThat(result.getName()).isEqualTo("Teren 1");
    }

    @Test
    void getById_nonExistingId_throwsException() {
        when(facilityRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> facilityService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_savesFacility() {
        when(facilityRepository.save(facility)).thenReturn(facility);

        Facility result = facilityService.create(facility);

        assertThat(result.getName()).isEqualTo("Teren 1");
        verify(facilityRepository, times(1)).save(facility);
    }

    @Test
    void delete_existingId_deletesSuccessfully() {
        when(facilityRepository.existsById(1L)).thenReturn(true);

        facilityService.delete(1L);

        verify(facilityRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(facilityRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> facilityService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}