package com.sportshub.analytics.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotNull(message = "Izvještaj ne smije biti null.")
    @ManyToOne
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @NotBlank(message = "Metrika ne smije biti prazna.")
    @Size(max = 100, message = "Metrika ne smije biti duža od 100 znakova.")
    @Column(nullable = false)
    private String metric;

    @NotNull(message = "Vrijednost ne smije biti null.")
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
