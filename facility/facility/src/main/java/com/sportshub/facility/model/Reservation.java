package com.sportshub.facility.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @NotNull(message = "ID korisnika ne smije biti null.")
    @Positive(message = "ID korisnika mora biti pozitivan broj.")
    @Column(nullable = false)
    private Long userId;

    @NotNull(message = "Raspored ne smije biti null.")
    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @NotNull(message = "Status rezervacije ne smije biti null.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    public Reservation(Long userId, Schedule schedule, ReservationStatus status) {
        this.userId = userId;
        this.schedule = schedule;
        this.status = status;
    }
}
