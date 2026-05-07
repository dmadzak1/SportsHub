package com.sportshub.promotion.controller;

import com.sportshub.promotion.dto.DiscountDTO;
import com.sportshub.promotion.model.Discount;
import com.sportshub.promotion.model.Promotion;
import com.sportshub.promotion.service.DiscountService;
import com.sportshub.promotion.service.PromotionService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/discounts")
public class DiscountController {

    private final DiscountService discountService;
    private final PromotionService promotionService;
    private final ModelMapper modelMapper;

    public DiscountController(DiscountService discountService,
                              PromotionService promotionService,
                              ModelMapper modelMapper) {
        this.discountService = discountService;
        this.promotionService = promotionService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<DiscountDTO> getAll() {
        return discountService.getAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public DiscountDTO getById(@PathVariable Long id) {
        return toDTO(discountService.getById(id));
    }

    @GetMapping("/promotion/{promotionId}")
    public List<DiscountDTO> getByPromotion(@PathVariable Long promotionId) {
        return discountService.getByPromotion(promotionId).stream()
                .map(this::toDTO)
                .toList();
    }

    @PostMapping
    public ResponseEntity<DiscountDTO> create(@Valid @RequestBody DiscountDTO dto) {
        Promotion promotion = promotionService.getById(dto.getPromotionId());
        Discount discount = new Discount(promotion, dto.getDescription());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toDTO(discountService.create(discount)));
    }

    @PutMapping("/{id}")
    public DiscountDTO update(@PathVariable Long id, @Valid @RequestBody DiscountDTO dto) {
        Discount partial = new Discount();
        partial.setDescription(dto.getDescription());
        return toDTO(discountService.update(id, partial));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        discountService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private DiscountDTO toDTO(Discount discount) {
        DiscountDTO dto = new DiscountDTO();
        dto.setDiscountId(discount.getDiscountId());
        dto.setPromotionId(discount.getPromotion().getPromotionId());
        dto.setDescription(discount.getDescription());
        return dto;
    }
}
