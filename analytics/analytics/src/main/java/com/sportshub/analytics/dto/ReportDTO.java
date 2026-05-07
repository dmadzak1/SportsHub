package com.sportshub.analytics.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReportDTO {

    private Long reportId;

    @NotBlank(message = "Tip izvještaja ne smije biti prazan.")
    private String reportType;

    private LocalDateTime generatedAt;
}
