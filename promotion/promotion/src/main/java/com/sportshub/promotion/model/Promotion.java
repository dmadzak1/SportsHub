package com.sportshub.promotion.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "promotions")
@Data
@NoArgsConstructor
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long promotionId;

    @ManyToOne
    @JoinColumn(name = "package_id", nullable = false)
    private Package pkg;

    @Column(nullable = false)
    private Double discount;

    private LocalDate validUntil;

    @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL)
    private List<Discount> discounts;

    public Promotion(Package pkg, Double discount, LocalDate validUntil) {
        this.pkg = pkg;
        this.discount = discount;
        this.validUntil = validUntil;
    }
}