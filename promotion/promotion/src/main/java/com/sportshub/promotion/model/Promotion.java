package com.sportshub.promotion.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Paket ne smije biti null.")
    @ManyToOne
    @JoinColumn(name = "package_id", nullable = false)
    private Package pkg;

    @NotNull(message = "Popust ne smije biti null.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Popust mora biti veći od 0.")
    @DecimalMax(value = "100.0", message = "Popust ne može biti veći od 100.")
    @Column(nullable = false)
    private Double discount;

    @NotNull(message = "Datum isteka promocije ne smije biti null.")
    @FutureOrPresent(message = "Datum isteka promocije ne može biti u prošlosti.")
    private LocalDate validUntil;

    @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL)
    private List<Discount> discounts;

    public Promotion(Package pkg, Double discount, LocalDate validUntil) {
        this.pkg = pkg;
        this.discount = discount;
        this.validUntil = validUntil;
    }
}