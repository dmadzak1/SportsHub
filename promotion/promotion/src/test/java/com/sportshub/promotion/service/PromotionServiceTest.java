package com.sportshub.promotion.service;

import com.sportshub.promotion.exception.ResourceNotFoundException;
import com.sportshub.promotion.model.Package;
import com.sportshub.promotion.model.Promotion;
import com.sportshub.promotion.repository.PromotionRepository;
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
class PromotionServiceTest {

    @Mock
    private PromotionRepository promotionRepository;

    @InjectMocks
    private PromotionService promotionService;

    private Package pkg;
    private Promotion promotion;

    @BeforeEach
    void setUp() {
        pkg = new Package("INDIVIDUAL", 99.99);
        pkg.setPackageId(1L);
        promotion = new Promotion(pkg, 10.0, LocalDate.of(2026, 12, 31));
        promotion.setPromotionId(1L);
    }

    @Test
    void getAll_returnsAllPromotions() {
        when(promotionRepository.findAll()).thenReturn(List.of(promotion));

        List<Promotion> result = promotionService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDiscount()).isEqualTo(10.0);
    }

    @Test
    void getById_existingId_returnsPromotion() {
        when(promotionRepository.findById(1L)).thenReturn(Optional.of(promotion));

        Promotion result = promotionService.getById(1L);

        assertThat(result.getDiscount()).isEqualTo(10.0);
    }

    @Test
    void getById_nonExistingId_throwsException() {
        when(promotionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> promotionService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getByPackage_returnsFilteredPromotions() {
        when(promotionRepository.findByPkgPackageId(1L)).thenReturn(List.of(promotion));

        List<Promotion> result = promotionService.getByPackage(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void getActive_returnsActivePromotions() {
        when(promotionRepository.findByValidUntilAfter(any(LocalDate.class))).thenReturn(List.of(promotion));

        List<Promotion> result = promotionService.getActive();

        assertThat(result).hasSize(1);
    }

    @Test
    void create_savesPromotion() {
        when(promotionRepository.save(promotion)).thenReturn(promotion);

        Promotion result = promotionService.create(promotion);

        assertThat(result.getDiscount()).isEqualTo(10.0);
        verify(promotionRepository, times(1)).save(promotion);
    }

    @Test
    void update_existingId_updatesFields() {
        Promotion updated = new Promotion();
        updated.setDiscount(20.0);
        updated.setValidUntil(LocalDate.of(2027, 6, 30));
        when(promotionRepository.findById(1L)).thenReturn(Optional.of(promotion));
        when(promotionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Promotion result = promotionService.update(1L, updated);

        assertThat(result.getDiscount()).isEqualTo(20.0);
        assertThat(result.getValidUntil()).isEqualTo(LocalDate.of(2027, 6, 30));
    }

    @Test
    void delete_existingId_deletesSuccessfully() {
        when(promotionRepository.existsById(1L)).thenReturn(true);

        promotionService.delete(1L);

        verify(promotionRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(promotionRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> promotionService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
