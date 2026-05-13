package com.sportshub.promotion.service;

import com.sportshub.promotion.exception.ResourceNotFoundException;
import com.sportshub.promotion.model.Package;
import com.sportshub.promotion.model.Promotion;
import com.sportshub.promotion.model.PromotionUsage;
import com.sportshub.promotion.repository.PromotionUsageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromotionUsageServiceTest {

    @Mock
    private PromotionUsageRepository promotionUsageRepository;

    @InjectMocks
    private PromotionUsageService promotionUsageService;

    private Promotion promotion;
    private PromotionUsage usage;

    @BeforeEach
    void setUp() {
        Package pkg = new Package("INDIVIDUAL", 99.99);
        pkg.setPackageId(1L);
        promotion = new Promotion(pkg, 10.0, LocalDate.of(2026, 12, 31));
        promotion.setPromotionId(1L);
        usage = new PromotionUsage(42L, promotion, 3);
        usage.setUsageId(1L);
    }

    @Test
    void getAll_returnsAllUsages() {
        when(promotionUsageRepository.findAll()).thenReturn(List.of(usage));

        List<PromotionUsage> result = promotionUsageService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsageCount()).isEqualTo(3);
    }

    @Test
    void getById_existingId_returnsUsage() {
        when(promotionUsageRepository.findById(1L)).thenReturn(Optional.of(usage));

        PromotionUsage result = promotionUsageService.getById(1L);

        assertThat(result.getUserId()).isEqualTo(42L);
    }

    @Test
    void getById_nonExistingId_throwsException() {
        when(promotionUsageRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> promotionUsageService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getByUser_returnsFilteredUsages() {
        when(promotionUsageRepository.findByUserId(42L)).thenReturn(List.of(usage));

        List<PromotionUsage> result = promotionUsageService.getByUser(42L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(42L);
    }

    @Test
    void getByPromotion_returnsFilteredUsages() {
        when(promotionUsageRepository.findByPromotionPromotionId(1L)).thenReturn(List.of(usage));

        List<PromotionUsage> result = promotionUsageService.getByPromotion(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void create_savesUsage() {
        when(promotionUsageRepository.save(usage)).thenReturn(usage);

        PromotionUsage result = promotionUsageService.create(usage);

        assertThat(result.getUsageCount()).isEqualTo(3);
        verify(promotionUsageRepository, times(1)).save(usage);
    }

    @Test
    void update_existingId_updatesUsageCount() {
        PromotionUsage updated = new PromotionUsage();
        updated.setUsageCount(5);
        when(promotionUsageRepository.findById(1L)).thenReturn(Optional.of(usage));
        when(promotionUsageRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PromotionUsage result = promotionUsageService.update(1L, updated);

        assertThat(result.getUsageCount()).isEqualTo(5);
    }

    @Test
    void delete_existingId_deletesSuccessfully() {
        when(promotionUsageRepository.existsById(1L)).thenReturn(true);

        promotionUsageService.delete(1L);

        verify(promotionUsageRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(promotionUsageRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> promotionUsageService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
