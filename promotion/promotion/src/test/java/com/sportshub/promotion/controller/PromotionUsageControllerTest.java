package com.sportshub.promotion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportshub.promotion.config.AppConfig;
import com.sportshub.promotion.dto.PromotionUsageDTO;
import com.sportshub.promotion.exception.GlobalExceptionHandler;
import com.sportshub.promotion.exception.ResourceNotFoundException;
import com.sportshub.promotion.model.Package;
import com.sportshub.promotion.model.Promotion;
import com.sportshub.promotion.model.PromotionUsage;
import com.sportshub.promotion.service.PromotionService;
import com.sportshub.promotion.service.PromotionUsageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PromotionUsageController.class)
@Import({GlobalExceptionHandler.class, AppConfig.class})
class PromotionUsageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PromotionUsageService promotionUsageService;

    @MockitoBean
    private PromotionService promotionService;

    @Autowired
    private ObjectMapper objectMapper;

    private Promotion promotion;
    private PromotionUsage usage;

    @BeforeEach
    void setUp() {
        Package pkg = new Package("INDIVIDUAL", 99.99);
        pkg.setPackageId(1L);
        promotion = new Promotion(pkg, 10.0, LocalDate.of(2026, 12, 31));
        promotion.setPromotionId(1L);
        usage = new PromotionUsage(42L, promotion, 3);
        usage.setUsageId(1L);
    }

    @Test
    void getAll_returnsListOfUsages() throws Exception {
        when(promotionUsageService.getAll()).thenReturn(List.of(usage));

        mockMvc.perform(get("/promotion-usages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(42))
                .andExpect(jsonPath("$[0].usageCount").value(3))
                .andExpect(jsonPath("$[0].promotionId").value(1));
    }

    @Test
    void getById_existingId_returnsUsage() throws Exception {
        when(promotionUsageService.getById(1L)).thenReturn(usage);

        mockMvc.perform(get("/promotion-usages/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usageCount").value(3));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(promotionUsageService.getById(99L))
                .thenThrow(new ResourceNotFoundException("PromotionUsage", 99L));

        mockMvc.perform(get("/promotion-usages/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not_found"));
    }

    @Test
    void create_validRequest_returns201() throws Exception {
        PromotionUsageDTO dto = new PromotionUsageDTO();
        dto.setUserId(42L);
        dto.setPromotionId(1L);
        dto.setUsageCount(3);

        when(promotionService.getById(1L)).thenReturn(promotion);
        when(promotionUsageService.create(any())).thenReturn(usage);

        mockMvc.perform(post("/promotion-usages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usageCount").value(3));
    }

    @Test
    void create_invalidRequest_returns400() throws Exception {
        PromotionUsageDTO dto = new PromotionUsageDTO();
        // userId, promotionId and usageCount are null

        mockMvc.perform(post("/promotion-usages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation"));
    }

    @Test
    void delete_existingId_returns204() throws Exception {
        mockMvc.perform(delete("/promotion-usages/1"))
                .andExpect(status().isNoContent());
    }
}
