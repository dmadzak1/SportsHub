package com.sportshub.analytics.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RevenueLogDTO {

    private Long revenueId;

    @NotNull(message = "ID statistike ne smije biti null.")
    private Long statId;

    @NotNull(message = "Datum ne smije biti null.")
    private LocalDate date;

    @NotNull(message = "Iznos ne smije biti null.")
    private Double amount;
}
