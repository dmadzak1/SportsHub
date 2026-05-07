package com.sportshub.promotion.service;

import com.sportshub.promotion.exception.ResourceNotFoundException;
import com.sportshub.promotion.model.Discount;
import com.sportshub.promotion.model.Package;
import com.sportshub.promotion.model.Promotion;
import com.sportshub.promotion.repository.DiscountRepository;
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
class DiscountServiceTest {

    @Mock
    private DiscountRepository discountRepository;

    @InjectMocks
    private DiscountService discountService;

    private Promotion promotion;
    private Discount discount;

    @BeforeEach
    void setUp() {
        Package pkg = new Package("INDIVIDUAL", 99.99);
        pkg.setPackageId(1L);
        promotion = new Promotion(pkg, 10.0, LocalDate.of(2026, 12, 31));
        promotion.setPromotionId(1L);
        discount = new Discount(promotion, "10% popusta za sve objekte");
        discount.setDiscountId(1L);
    }

    @Test
    void getAll_returnsAllDiscounts() {
        when(discountRepository.findAll()).thenReturn(List.of(discount));

        List<Discount> result = discountService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).isEqualTo("10% popusta za sve objekte");
    }

    @Test
    void getById_existingId_returnsDiscount() {
        when(discountRepository.findById(1L)).thenReturn(Optional.of(discount));

        Discount result = discountService.getById(1L);

        assertThat(result.getDescription()).isEqualTo("10% popusta za sve objekte");
    }

    @Test
    void getById_nonExistingId_throwsException() {
        when(discountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> discountService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getByPromotion_returnsFilteredDiscounts() {
        when(discountRepository.findByPromotionPromotionId(1L)).thenReturn(List.of(discount));

        List<Discount> result = discountService.getByPromotion(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void create_savesDiscount() {
        when(discountRepository.save(discount)).thenReturn(discount);

        Discount result = discountService.create(discount);

        assertThat(result.getDescription()).isEqualTo("10% popusta za sve objekte");
        verify(discountRepository, times(1)).save(discount);
    }

    @Test
    void update_existingId_updatesDescription() {
        Discount updated = new Discount();
        updated.setDescription("20% popusta za grupne rezervacije");
        when(discountRepository.findById(1L)).thenReturn(Optional.of(discount));
        when(discountRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Discount result = discountService.update(1L, updated);

        assertThat(result.getDescription()).isEqualTo("20% popusta za grupne rezervacije");
    }

    @Test
    void delete_existingId_deletesSuccessfully() {
        when(discountRepository.existsById(1L)).thenReturn(true);

        discountService.delete(1L);

        verify(discountRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(discountRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> discountService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
