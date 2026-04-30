package com.sportshub.promotion.repository;

import com.sportshub.promotion.model.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    List<Promotion> findByPkgPackageId(Long packageId);
    List<Promotion> findByValidUntilAfter(LocalDate date);
}