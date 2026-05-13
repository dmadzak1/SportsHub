package com.sportshub.facility.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "facility", fetch = FetchType.LAZY)
    private List<Schedule> schedules = new ArrayList<>();

    public Facility(String name, String type) {
        this.name = name;
        this.type = type;
    }
}