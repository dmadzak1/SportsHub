package com.sportshub.facility.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "schedules")
@Data
@NoArgsConstructor
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @NotNull(message = "Objekt ne smije biti null.")
    @ManyToOne
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @NotNull(message = "Datum ne smije biti null.")
    @FutureOrPresent(message = "Datum ne može biti u prošlosti.")
    @Column(nullable = false)
    private LocalDate date;

    @NotNull(message = "Termin ne smije biti null.")
    @Column(nullable = false)
    private LocalTime timeSlot;

    public Schedule(Facility facility, LocalDate date, LocalTime timeSlot) {
        this.facility = facility;
        this.date = date;
        this.timeSlot = timeSlot;
    }
}
