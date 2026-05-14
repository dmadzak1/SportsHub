package com.sportshub.analytics.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "revenue_logs")
@Data
@NoArgsConstructor
public class RevenueLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long revenueId;

    @NotNull(message = "Statistika ne smije biti null.")
    @ManyToOne
    @JoinColumn(name = "stat_id", nullable = false)
    private Statistics statistics;

    @NotNull(message = "Datum ne smije biti null.")
    @Column(nullable = false)
    private LocalDate date;

    @NotNull(message = "Iznos ne smije biti null.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Iznos mora biti veći od 0.")
    @Column(nullable = false)
    private Double amount;

    public RevenueLog(Statistics statistics, LocalDate date, Double amount) {
        this.statistics = statistics;
        this.date = date;
        this.amount = amount;
    }
}