package com.sportshub.analytics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatisticsDTO {

    private Long statId;

    @NotNull(message = "ID izvještaja ne smije biti null.")
    private Long reportId;

    @NotBlank(message = "Metrika ne smije biti prazna.")
    private String metric;

    @NotNull(message = "Vrijednost ne smije biti null.")
    private Double value;
}
