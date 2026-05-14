package com.example.user.client;

import com.example.user.dto.PromotionResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class PromotionServiceClientFallback implements PromotionServiceClient {

    private static final Logger log = LoggerFactory.getLogger(PromotionServiceClientFallback.class);

    @Override
    public List<PromotionResponseDTO> getActivePromotions() {
        log.error("Promotion servis nije dostupan, fallback za getActivePromotions");
        return Collections.emptyList();
    }
}