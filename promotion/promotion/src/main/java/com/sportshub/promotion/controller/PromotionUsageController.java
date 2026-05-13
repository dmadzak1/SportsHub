package com.sportshub.promotion.controller;

import com.sportshub.promotion.dto.PromotionUsageDTO;
import com.sportshub.promotion.model.Promotion;
import com.sportshub.promotion.model.PromotionUsage;
import com.sportshub.promotion.service.PromotionService;
import com.sportshub.promotion.service.PromotionUsageService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/promotion-usages")
public class PromotionUsageController {

    private final PromotionUsageService promotionUsageService;
    private final PromotionService promotionService;
    private final ModelMapper modelMapper;

    public PromotionUsageController(PromotionUsageService promotionUsageService,
                                    PromotionService promotionService,
                                    ModelMapper modelMapper) {
        this.promotionUsageService = promotionUsageService;
        this.promotionService = promotionService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<PromotionUsageDTO> getAll() {
        return promotionUsageService.getAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public PromotionUsageDTO getById(@PathVariable Long id) {
        return toDTO(promotionUsageService.getById(id));
    }

    @GetMapping("/user/{userId}")
    public List<PromotionUsageDTO> getByUser(@PathVariable Long userId) {
        return promotionUsageService.getByUser(userId).stream()
                .map(this::toDTO)
                .toList();
    }

    @GetMapping("/promotion/{promotionId}")
    public List<PromotionUsageDTO> getByPromotion(@PathVariable Long promotionId) {
        return promotionUsageService.getByPromotion(promotionId).stream()
                .map(this::toDTO)
                .toList();
    }

    @PostMapping
    public ResponseEntity<PromotionUsageDTO> create(@Valid @RequestBody PromotionUsageDTO dto) {
        Promotion promotion = promotionService.getById(dto.getPromotionId());
        PromotionUsage usage = new PromotionUsage(dto.getUserId(), promotion, dto.getUsageCount());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toDTO(promotionUsageService.create(usage)));
    }

    @PatchMapping("/{id}/count")
    public PromotionUsageDTO updateCount(@PathVariable Long id, @RequestParam Integer count) {
        PromotionUsage partial = new PromotionUsage();
        partial.setUsageCount(count);
        return toDTO(promotionUsageService.update(id, partial));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        promotionUsageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private PromotionUsageDTO toDTO(PromotionUsage usage) {
        PromotionUsageDTO dto = new PromotionUsageDTO();
        dto.setUsageId(usage.getUsageId());
        dto.setUserId(usage.getUserId());
        dto.setPromotionId(usage.getPromotion().getPromotionId());
        dto.setUsageCount(usage.getUsageCount());
        return dto;
    }
}
