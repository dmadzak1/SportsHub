package com.sportshub.analytics.model;

import jakarta.persistence.*;
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

    @ManyToOne
    @JoinColumn(name = "stat_id", nullable = false)
    private Statistics statistics;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Double amount;

    public RevenueLog(Statistics statistics, LocalDate date, Double amount) {
        this.statistics = statistics;
        this.date = date;
        this.amount = amount;
    }
}