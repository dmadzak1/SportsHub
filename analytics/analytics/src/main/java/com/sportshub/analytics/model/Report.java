package com.sportshub.analytics.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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

    @NotBlank(message = "Naziv izvještaja ne smije biti prazan.")
    @Size(max = 100, message = "Naziv izvještaja ne smije biti duži od 100 znakova.")
    @Column(nullable = false)
    private String title;

    @NotNull(message = "Datum izvještaja ne smije biti null.")
    @Column(nullable = false)
    private LocalDate date;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL)
    private List<Statistics> statistics;

    public Report(String title, LocalDate date) {
        this.title = title;
        this.date = date;
    }
}