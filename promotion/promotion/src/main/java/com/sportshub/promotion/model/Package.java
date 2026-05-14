package com.sportshub.promotion.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "packages")
@Data
@NoArgsConstructor
public class Package {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long packageId;

    @NotBlank(message = "Naziv paketa ne smije biti prazan.")
    @Size(max = 50, message = "Naziv paketa ne smije biti duži od 50 znakova.")
    @Column(nullable = false)
    private String name; // INDIVIDUAL, GROUP, FAMILY

    @NotNull(message = "Cijena ne smije biti null.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Cijena mora biti veća od 0.")
    @Column(nullable = false)
    private Double price;

    @OneToMany(mappedBy = "pkg", cascade = CascadeType.ALL)
    private List<Promotion> promotions;

    public Package(String name, Double price) {
        this.name = name;
        this.price = price;
    }
}