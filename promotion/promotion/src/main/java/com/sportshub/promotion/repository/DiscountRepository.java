package com.sportshub.promotion.repository;

import com.sportshub.promotion.model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    List<Discount> findByPromotionPromotionId(Long promotionId);
}
