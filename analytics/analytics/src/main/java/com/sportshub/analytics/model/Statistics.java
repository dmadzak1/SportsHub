package com.sportshub.analytics.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "statistics")
@Data
@NoArgsConstructor
public class Statistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statId;

    @ManyToOne
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @Column(nullable = false)
    private String metric; // TOTAL_RESERVATIONS, OCCUPANCY_RATE, TOTAL_REVENUE

    @Column(nullable = false)
    private Double value;

    @OneToMany(mappedBy = "statistics", cascade = CascadeType.ALL)
    private List<RevenueLog> revenueLogs;

    public Statistics(Report report, String metric, Double value) {
        this.report = report;
        this.metric = metric;
        this.value = value;
    }
}
