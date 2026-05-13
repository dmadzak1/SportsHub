package com.sportshub.promotion.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PromotionDTO {

    private Long promotionId;

    @NotNull(message = "ID paketa ne smije biti null.")
    private Long packageId;

    @NotNull(message = "Popust ne smije biti null.")
    private Double discount;

    private LocalDate validUntil;
}
