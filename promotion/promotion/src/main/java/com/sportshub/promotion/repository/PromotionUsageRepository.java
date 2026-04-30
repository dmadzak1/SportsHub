package com.sportshub.promotion.repository;

import com.sportshub.promotion.model.PromotionUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromotionUsageRepository extends JpaRepository<PromotionUsage, Long> {
    List<PromotionUsage> findByUserId(Long userId);
    List<PromotionUsage> findByPromotionPromotionId(Long promotionId);
}
