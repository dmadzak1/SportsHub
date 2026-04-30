package com.sportshub.promotion.model;

import jakarta.persistence.*;
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

    @Column(nullable = false)
    private String name; // INDIVIDUAL, GROUP, FAMILY

    @Column(nullable = false)
    private Double price;

    @OneToMany(mappedBy = "pkg", cascade = CascadeType.ALL)
    private List<Promotion> promotions;

    public Package(String name, Double price) {
        this.name = name;
        this.price = price;
    }
}