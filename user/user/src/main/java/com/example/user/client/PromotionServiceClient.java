package com.example.user.client;

import com.example.user.dto.PromotionResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "promotion", fallback = PromotionServiceClientFallback.class)
public interface PromotionServiceClient {

    @GetMapping("/promotions/active")
    List<PromotionResponseDTO> getActivePromotions();
}