package com.sportshub.promotion.service;

import com.sportshub.promotion.exception.ResourceNotFoundException;
import com.sportshub.promotion.model.Package;
import com.sportshub.promotion.repository.PackageRepository;
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
class PackageServiceTest {

    @Mock
    private PackageRepository packageRepository;

    @InjectMocks
    private PackageService packageService;

    private Package pkg;

    @BeforeEach
    void setUp() {
        pkg = new Package("INDIVIDUAL", 99.99);
        pkg.setPackageId(1L);
    }

    @Test
    void getAll_returnsAllPackages() {
        when(packageRepository.findAll()).thenReturn(List.of(pkg));

        List<Package> result = packageService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("INDIVIDUAL");
    }

    @Test
    void getById_existingId_returnsPackage() {
        when(packageRepository.findById(1L)).thenReturn(Optional.of(pkg));

        Package result = packageService.getById(1L);

        assertThat(result.getPrice()).isEqualTo(99.99);
    }

    @Test
    void getById_nonExistingId_throwsException() {
        when(packageRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> packageService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getByName_returnsFilteredPackages() {
        when(packageRepository.findByName("INDIVIDUAL")).thenReturn(List.of(pkg));

        List<Package> result = packageService.getByName("individual");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("INDIVIDUAL");
    }

    @Test
    void create_savesPackage() {
        when(packageRepository.save(pkg)).thenReturn(pkg);

        Package result = packageService.create(pkg);

        assertThat(result.getName()).isEqualTo("INDIVIDUAL");
        verify(packageRepository, times(1)).save(pkg);
    }

    @Test
    void update_existingId_updatesFields() {
        Package updated = new Package("GROUP", 149.99);
        when(packageRepository.findById(1L)).thenReturn(Optional.of(pkg));
        when(packageRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Package result = packageService.update(1L, updated);

        assertThat(result.getName()).isEqualTo("GROUP");
        assertThat(result.getPrice()).isEqualTo(149.99);
    }

    @Test
    void delete_existingId_deletesSuccessfully() {
        when(packageRepository.existsById(1L)).thenReturn(true);

        packageService.delete(1L);

        verify(packageRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(packageRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> packageService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
