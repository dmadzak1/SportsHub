package com.sportshub.promotion.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "discounts")
@Data
@NoArgsConstructor
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long discountId;

    @NotNull(message = "Promocija ne smije biti null.")
    @ManyToOne
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

    @NotBlank(message = "Opis popusta ne smije biti prazan.")
    @Size(max = 255, message = "Opis popusta ne smije biti duži od 255 znakova.")
    @Column(nullable = false)
    private String description;

    public Discount(Promotion promotion, String description) {
        this.promotion = promotion;
        this.description = description;
    }
}