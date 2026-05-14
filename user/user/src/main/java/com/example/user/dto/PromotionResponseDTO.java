package com.example.user.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PromotionResponseDTO {
    private Long promotionId;
    private Double discount;
    private LocalDate validUntil;
    private Long packageId;
}