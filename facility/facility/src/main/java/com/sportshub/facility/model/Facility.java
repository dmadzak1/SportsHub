package com.sportshub.facility.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Naziv objekta ne smije biti prazan.")
    @Size(max = 100, message = "Naziv objekta ne smije biti duži od 100 znakova.")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Tip objekta ne smije biti prazan.")
    @Size(max = 50, message = "Tip objekta ne smije biti duži od 50 znakova.")
    @Column(nullable = false)
    private String type; // TENNIS, SQUASH, POOL, GYM

    @OneToMany(mappedBy = "facility", fetch = FetchType.LAZY)
    private List<Schedule> schedules = new ArrayList<>();

    public Facility(String name, String type) {
        this.name = name;
        this.type = type;
    }
}