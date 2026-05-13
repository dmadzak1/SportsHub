package com.sportshub.promotion.service;

import com.sportshub.promotion.exception.ResourceNotFoundException;
import com.sportshub.promotion.model.PromotionUsage;
import com.sportshub.promotion.repository.PromotionUsageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromotionUsageService {

    private final PromotionUsageRepository promotionUsageRepository;

    public PromotionUsageService(PromotionUsageRepository promotionUsageRepository) {
        this.promotionUsageRepository = promotionUsageRepository;
    }

    public List<PromotionUsage> getAll() {
        return promotionUsageRepository.findAll();
    }

    public PromotionUsage getById(Long id) {
        return promotionUsageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PromotionUsage", id));
    }

    public List<PromotionUsage> getByUser(Long userId) {
        return promotionUsageRepository.findByUserId(userId);
    }

    public List<PromotionUsage> getByPromotion(Long promotionId) {
        return promotionUsageRepository.findByPromotionPromotionId(promotionId);
    }

    public PromotionUsage create(PromotionUsage usage) {
        return promotionUsageRepository.save(usage);
    }

    public PromotionUsage update(Long id, PromotionUsage updated) {
        PromotionUsage existing = getById(id);
        existing.setUsageCount(updated.getUsageCount());
        return promotionUsageRepository.save(existing);
    }

    public void delete(Long id) {
        if (!promotionUsageRepository.existsById(id)) {
            throw new ResourceNotFoundException("PromotionUsage", id);
        }
        promotionUsageRepository.deleteById(id);
    }
}
