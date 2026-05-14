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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
        assertThat(facilityService.getAll()).hasSize(1);
    }

    @Test
    void getById_existingId_returnsFacility() {
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));
        assertThat(facilityService.getById(1L).getName()).isEqualTo("Teren 1");
    }

    @Test
    void getById_nonExistingId_throwsException() {
        when(facilityRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> facilityService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getPaginated_returnsPage() {
        var page = new PageImpl<>(List.of(facility), PageRequest.of(0, 5), 1);
        when(facilityRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(page);
        assertThat(facilityService.getPaginated(0, 5, "facilityId").getContent()).hasSize(1);
    }

    @Test
    void searchByName_returnsResults() {
        when(facilityRepository.searchByName("Teren")).thenReturn(List.of(facility));
        assertThat(facilityService.searchByName("Teren")).hasSize(1);
    }

    @Test
    void createBatch_savesAll() {
        when(facilityRepository.saveAll(any())).thenReturn(List.of(facility));
        assertThat(facilityService.createBatch(List.of(facility))).hasSize(1);
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