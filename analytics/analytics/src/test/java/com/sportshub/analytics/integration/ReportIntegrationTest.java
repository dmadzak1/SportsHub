package com.sportshub.analytics.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportshub.analytics.repository.ReportRepository;
import com.sportshub.analytics.repository.RevenueLogRepository;
import com.sportshub.analytics.repository.StatisticsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReportIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RevenueLogRepository revenueLogRepository;

    @Autowired
    private StatisticsRepository statisticsRepository;

    @Autowired
    private ReportRepository reportRepository;

    @BeforeEach
    void setUp() {
        revenueLogRepository.deleteAll();
        statisticsRepository.deleteAll();
        reportRepository.deleteAll();
    }

    @Test
    void createReport_thenFetchById() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "reportType", "REVENUE"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reportId").isNumber())
                .andExpect(jsonPath("$.reportType").value("REVENUE"))
                .andReturn();

        JsonNode created = objectMapper.readTree(createResult.getResponse().getContentAsString());
        long reportId = created.get("reportId").asLong();

        mockMvc.perform(get("/reports/{id}", reportId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reportId").value((int) reportId))
                .andExpect(jsonPath("$.reportType").value("REVENUE"));
    }

    @Test
    void createReport_thenFilterByType() throws Exception {
        mockMvc.perform(post("/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "reportType", "CAPACITY"
                        ))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/reports/type/CAPACITY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reportType").value("CAPACITY"));
    }
}
