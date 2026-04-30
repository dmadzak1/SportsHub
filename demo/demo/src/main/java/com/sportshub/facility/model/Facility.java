package com.sportshub.facility.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "facilities")
@Data
@NoArgsConstructor
public class Facility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facilityId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type; // TENNIS, SQUASH, POOL, GYM

    public Facility(String name, String type) {
        this.name = name;
        this.type = type;
    }
}