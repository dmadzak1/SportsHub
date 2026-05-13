package com.sportshub.promotion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportshub.promotion.config.AppConfig;
import com.sportshub.promotion.dto.DiscountDTO;
import com.sportshub.promotion.exception.GlobalExceptionHandler;
import com.sportshub.promotion.exception.ResourceNotFoundException;
import com.sportshub.promotion.model.Discount;
import com.sportshub.promotion.model.Package;
import com.sportshub.promotion.model.Promotion;
import com.sportshub.promotion.service.DiscountService;
import com.sportshub.promotion.service.PromotionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
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

@WebMvcTest(DiscountController.class)
@Import({GlobalExceptionHandler.class, AppConfig.class})
class DiscountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DiscountService discountService;

    @MockitoBean
    private PromotionService promotionService;

    @Autowired
    private ObjectMapper objectMapper;

    private Promotion promotion;
    private Discount discount;

    @BeforeEach
    void setUp() {
        Package pkg = new Package("INDIVIDUAL", 99.99);
        pkg.setPackageId(1L);
        promotion = new Promotion(pkg, 10.0, LocalDate.of(2026, 12, 31));
        promotion.setPromotionId(1L);
        discount = new Discount(promotion, "10% popusta za sve objekte");
        discount.setDiscountId(1L);
    }

    @Test
    void getAll_returnsListOfDiscounts() throws Exception {
        when(discountService.getAll()).thenReturn(List.of(discount));

        mockMvc.perform(get("/discounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("10% popusta za sve objekte"))
                .andExpect(jsonPath("$[0].promotionId").value(1));
    }

    @Test
    void getById_existingId_returnsDiscount() throws Exception {
        when(discountService.getById(1L)).thenReturn(discount);

        mockMvc.perform(get("/discounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("10% popusta za sve objekte"));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(discountService.getById(99L))
                .thenThrow(new ResourceNotFoundException("Discount", 99L));

        mockMvc.perform(get("/discounts/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not_found"));
    }

    @Test
    void create_validRequest_returns201() throws Exception {
        DiscountDTO dto = new DiscountDTO();
        dto.setPromotionId(1L);
        dto.setDescription("10% popusta za sve objekte");

        when(promotionService.getById(1L)).thenReturn(promotion);
        when(discountService.create(any())).thenReturn(discount);

        mockMvc.perform(post("/discounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("10% popusta za sve objekte"));
    }

    @Test
    void create_invalidRequest_returns400() throws Exception {
        DiscountDTO dto = new DiscountDTO();
        // promotionId is null, description is blank

        mockMvc.perform(post("/discounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation"));
    }

    @Test
    void delete_existingId_returns204() throws Exception {
        mockMvc.perform(delete("/discounts/1"))
                .andExpect(status().isNoContent());
    }
}
