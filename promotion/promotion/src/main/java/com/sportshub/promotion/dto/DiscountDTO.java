package com.sportshub.promotion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DiscountDTO {

    private Long discountId;

    @NotNull(message = "ID promocije ne smije biti null.")
    private Long promotionId;

    @NotBlank(message = "Opis ne smije biti prazan.")
    private String description;
}
