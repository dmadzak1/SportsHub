package com.sportshub.promotion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportshub.promotion.config.AppConfig;
import com.sportshub.promotion.dto.PromotionDTO;
import com.sportshub.promotion.exception.GlobalExceptionHandler;
import com.sportshub.promotion.exception.ResourceNotFoundException;
import com.sportshub.promotion.model.Package;
import com.sportshub.promotion.model.Promotion;
import com.sportshub.promotion.service.PackageService;
import com.sportshub.promotion.service.PromotionService;
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

@WebMvcTest(PromotionController.class)
@Import({GlobalExceptionHandler.class, AppConfig.class})
class PromotionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PromotionService promotionService;

    @MockitoBean
    private PackageService packageService;

    @Autowired
    private ObjectMapper objectMapper;

    private Package pkg;
    private Promotion promotion;

    @BeforeEach
    void setUp() {
        pkg = new Package("INDIVIDUAL", 99.99);
        pkg.setPackageId(1L);
        promotion = new Promotion(pkg, 10.0, LocalDate.of(2026, 12, 31));
        promotion.setPromotionId(1L);
    }

    @Test
    void getAll_returnsListOfPromotions() throws Exception {
        when(promotionService.getAll()).thenReturn(List.of(promotion));

        mockMvc.perform(get("/promotions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].discount").value(10.0))
                .andExpect(jsonPath("$[0].packageId").value(1));
    }

    @Test
    void getById_existingId_returnsPromotion() throws Exception {
        when(promotionService.getById(1L)).thenReturn(promotion);

        mockMvc.perform(get("/promotions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.discount").value(10.0));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(promotionService.getById(99L))
                .thenThrow(new ResourceNotFoundException("Promotion", 99L));

        mockMvc.perform(get("/promotions/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not_found"));
    }

    @Test
    void create_validRequest_returns201() throws Exception {
        PromotionDTO dto = new PromotionDTO();
        dto.setPackageId(1L);
        dto.setDiscount(10.0);
        dto.setValidUntil(LocalDate.of(2026, 12, 31));

        when(packageService.getById(1L)).thenReturn(pkg);
        when(promotionService.create(any())).thenReturn(promotion);

        mockMvc.perform(post("/promotions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.discount").value(10.0));
    }

    @Test
    void create_invalidRequest_returns400() throws Exception {
        PromotionDTO dto = new PromotionDTO();
        // packageId and discount are null

        mockMvc.perform(post("/promotions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation"));
    }

    @Test
    void delete_existingId_returns204() throws Exception {
        mockMvc.perform(delete("/promotions/1"))
                .andExpect(status().isNoContent());
    }
}
