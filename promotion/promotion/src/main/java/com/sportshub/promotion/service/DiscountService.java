package com.sportshub.promotion.service;

import com.sportshub.promotion.exception.ResourceNotFoundException;
import com.sportshub.promotion.model.Discount;
import com.sportshub.promotion.repository.DiscountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscountService {

    private final DiscountRepository discountRepository;

    public DiscountService(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    public List<Discount> getAll() {
        return discountRepository.findAll();
    }

    public Discount getById(Long id) {
        return discountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discount", id));
    }

    public List<Discount> getByPromotion(Long promotionId) {
        return discountRepository.findByPromotionPromotionId(promotionId);
    }

    public Discount create(Discount discount) {
        return discountRepository.save(discount);
    }

    public Discount update(Long id, Discount updated) {
        Discount existing = getById(id);
        existing.setDescription(updated.getDescription());
        return discountRepository.save(existing);
    }

    public void delete(Long id) {
        if (!discountRepository.existsById(id)) {
            throw new ResourceNotFoundException("Discount", id);
        }
        discountRepository.deleteById(id);
    }
}
