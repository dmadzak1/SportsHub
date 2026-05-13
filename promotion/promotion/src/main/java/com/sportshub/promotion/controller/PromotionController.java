package com.sportshub.promotion.controller;

import com.sportshub.promotion.dto.PromotionDTO;
import com.sportshub.promotion.model.Package;
import com.sportshub.promotion.model.Promotion;
import com.sportshub.promotion.service.PackageService;
import com.sportshub.promotion.service.PromotionService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/promotions")
public class PromotionController {

    private final PromotionService promotionService;
    private final PackageService packageService;
    private final ModelMapper modelMapper;

    public PromotionController(PromotionService promotionService,
                               PackageService packageService,
                               ModelMapper modelMapper) {
        this.promotionService = promotionService;
        this.packageService = packageService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<PromotionDTO> getAll() {
        return promotionService.getAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public PromotionDTO getById(@PathVariable Long id) {
        return toDTO(promotionService.getById(id));
    }

    @GetMapping("/package/{packageId}")
    public List<PromotionDTO> getByPackage(@PathVariable Long packageId) {
        return promotionService.getByPackage(packageId).stream()
                .map(this::toDTO)
                .toList();
    }

    @GetMapping("/active")
    public List<PromotionDTO> getActive() {
        return promotionService.getActive().stream()
                .map(this::toDTO)
                .toList();
    }

    @PostMapping
    public ResponseEntity<PromotionDTO> create(@Valid @RequestBody PromotionDTO dto) {
        Package pkg = packageService.getById(dto.getPackageId());
        Promotion promotion = new Promotion(pkg, dto.getDiscount(), dto.getValidUntil());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toDTO(promotionService.create(promotion)));
    }

    @PutMapping("/{id}")
    public PromotionDTO update(@PathVariable Long id, @Valid @RequestBody PromotionDTO dto) {
        Promotion partial = new Promotion();
        partial.setDiscount(dto.getDiscount());
        partial.setValidUntil(dto.getValidUntil());
        return toDTO(promotionService.update(id, partial));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        promotionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private PromotionDTO toDTO(Promotion promotion) {
        PromotionDTO dto = new PromotionDTO();
        dto.setPromotionId(promotion.getPromotionId());
        dto.setPackageId(promotion.getPkg().getPackageId());
        dto.setDiscount(promotion.getDiscount());
        dto.setValidUntil(promotion.getValidUntil());
        return dto;
    }
}
