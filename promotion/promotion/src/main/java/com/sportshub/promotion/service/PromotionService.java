package com.sportshub.promotion.service;

import com.sportshub.promotion.exception.ResourceNotFoundException;
import com.sportshub.promotion.model.Promotion;
import com.sportshub.promotion.repository.PromotionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PromotionService {

    private final PromotionRepository promotionRepository;

    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    public List<Promotion> getAll() {
        return promotionRepository.findAll();
    }

    public Promotion getById(Long id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion", id));
    }

    public List<Promotion> getByPackage(Long packageId) {
        return promotionRepository.findByPkgPackageId(packageId);
    }

    public List<Promotion> getActive() {
        return promotionRepository.findByValidUntilAfter(LocalDate.now());
    }

    public Promotion create(Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    public Promotion update(Long id, Promotion updated) {
        Promotion existing = getById(id);
        existing.setDiscount(updated.getDiscount());
        existing.setValidUntil(updated.getValidUntil());
        return promotionRepository.save(existing);
    }

    public void delete(Long id) {
        if (!promotionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Promotion", id);
        }
        promotionRepository.deleteById(id);
    }
}
