package com.sportshub.promotion.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportshub.promotion.model.Package;
import com.sportshub.promotion.repository.DiscountRepository;
import com.sportshub.promotion.repository.PackageRepository;
import com.sportshub.promotion.repository.PromotionRepository;
import com.sportshub.promotion.repository.PromotionUsageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PromotionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private PromotionUsageRepository promotionUsageRepository;

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private PackageRepository packageRepository;

    @BeforeEach
    void setUp() {
        discountRepository.deleteAll();
        promotionUsageRepository.deleteAll();
        promotionRepository.deleteAll();
        packageRepository.deleteAll();
    }

    @Test
    void createPromotion_thenFetchById() throws Exception {
        Package savedPackage = packageRepository.save(new Package("INTEGRATION_PACKAGE", 99.0));

        mockMvc.perform(post("/promotions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "packageId", savedPackage.getPackageId(),
                                "discount", 15.0,
                                "validUntil", LocalDate.now().plusDays(10)
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.packageId").value(savedPackage.getPackageId().intValue()))
                .andExpect(jsonPath("$.discount").value(15.0));
    }

    @Test
    void createPromotion_thenGetByPackage() throws Exception {
        Package savedPackage = packageRepository.save(new Package("GROUP", 149.0));

        mockMvc.perform(post("/promotions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "packageId", savedPackage.getPackageId(),
                                "discount", 25.0,
                                "validUntil", LocalDate.now().plusDays(30)
                        ))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/promotions/package/{packageId}", savedPackage.getPackageId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].discount").value(25.0));
    }
}
