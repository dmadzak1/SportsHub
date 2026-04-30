package com.sportshub.promotion.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "promotion_usages")
@Data
@NoArgsConstructor
public class PromotionUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long usageId;

    @Column(nullable = false)
    private Long userId; // referenca na User Service

    @ManyToOne
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

    @Column(nullable = false)
    private Integer usageCount;

    public PromotionUsage(Long userId, Promotion promotion, Integer usageCount) {
        this.userId = userId;
        this.promotion = promotion;
        this.usageCount = usageCount;
    }
}
