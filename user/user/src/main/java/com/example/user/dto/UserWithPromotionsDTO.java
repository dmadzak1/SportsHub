package com.example.user.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserWithPromotionsDTO {
    private Long userId;
    private String email;
    private Long roleId;
    private List<PromotionResponseDTO> promotions;
}