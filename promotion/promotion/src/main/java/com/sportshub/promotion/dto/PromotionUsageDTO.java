package com.sportshub.promotion.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PromotionUsageDTO {

    private Long usageId;

    @NotNull(message = "ID korisnika ne smije biti null.")
    private Long userId;

    @NotNull(message = "ID promocije ne smije biti null.")
    private Long promotionId;

    @NotNull(message = "Broj upotreba ne smije biti null.")
    private Integer usageCount;
}
