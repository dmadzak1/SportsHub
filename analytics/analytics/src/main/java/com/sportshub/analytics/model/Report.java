package com.sportshub.analytics.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @Column(nullable = false)
    private String reportType; // REVENUE, CAPACITY, RESERVATIONS

    private LocalDateTime generatedAt;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL)
    private List<Statistics> statistics;

    public Report(String reportType) {
        this.reportType = reportType;
        this.generatedAt = LocalDateTime.now();
    }
}