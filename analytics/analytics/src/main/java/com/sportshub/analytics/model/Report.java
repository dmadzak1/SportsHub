package com.sportshub.analytics.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @NotBlank(message = "Tip izvještaja ne smije biti prazan.")
    @Column(nullable = false)
    private String reportType;

    @Column(nullable = false)
    private LocalDateTime generatedAt;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL)
    private List<Statistics> statistics = new ArrayList<>();

    public Report(String reportType) {
        this.reportType = reportType;
        this.generatedAt = LocalDateTime.now();
    }

    public Report(String reportType, LocalDateTime generatedAt) {
        this.reportType = reportType;
        this.generatedAt = generatedAt;
    }
}