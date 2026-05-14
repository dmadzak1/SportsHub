package com.sportshub.facility.model;

import jakarta.persistence.*;
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

    // Long umjesto @ManyToOne jer je User u zasebnom mikroservisu
    @Column(nullable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    public Reservation(Long userId, Schedule schedule, ReservationStatus status) {
        this.userId = userId;
        this.schedule = schedule;
        this.status = status;
    }
}
