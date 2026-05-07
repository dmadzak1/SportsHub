package com.sportshub.analytics.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportshub.analytics.config.AppConfig;
import com.sportshub.analytics.dto.RevenueLogDTO;
import com.sportshub.analytics.exception.GlobalExceptionHandler;
import com.sportshub.analytics.exception.ResourceNotFoundException;
import com.sportshub.analytics.model.Report;
import com.sportshub.analytics.model.RevenueLog;
import com.sportshub.analytics.model.Statistics;
import com.sportshub.analytics.service.RevenueLogService;
import com.sportshub.analytics.service.StatisticsService;
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

@WebMvcTest(RevenueLogController.class)
@Import({GlobalExceptionHandler.class, AppConfig.class})
class RevenueLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RevenueLogService revenueLogService;

    @MockitoBean
    private StatisticsService statisticsService;

    @Autowired
    private ObjectMapper objectMapper;

    private Statistics stat;
    private RevenueLog log;

    @BeforeEach
    void setUp() {
        Report report = new Report("REVENUE");
        report.setReportId(1L);
        stat = new Statistics(report, "TOTAL_REVENUE", 1000.0);
        stat.setStatId(1L);
        log = new RevenueLog(stat, LocalDate.of(2025, 6, 1), 500.0);
        log.setRevenueId(1L);
    }

    @Test
    void getAll_returnsListOfRevenueLogs() throws Exception {
        when(revenueLogService.getAll()).thenReturn(List.of(log));

        mockMvc.perform(get("/revenue-logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(500.0))
                .andExpect(jsonPath("$[0].statId").value(1));
    }

    @Test
    void getById_existingId_returnsRevenueLog() throws Exception {
        when(revenueLogService.getById(1L)).thenReturn(log);

        mockMvc.perform(get("/revenue-logs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(500.0));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(revenueLogService.getById(99L))
                .thenThrow(new ResourceNotFoundException("RevenueLog", 99L));

        mockMvc.perform(get("/revenue-logs/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not_found"));
    }

    @Test
    void create_validRequest_returns201() throws Exception {
        RevenueLogDTO dto = new RevenueLogDTO();
        dto.setStatId(1L);
        dto.setDate(LocalDate.of(2025, 6, 1));
        dto.setAmount(500.0);

        when(statisticsService.getById(1L)).thenReturn(stat);
        when(revenueLogService.create(any())).thenReturn(log);

        mockMvc.perform(post("/revenue-logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(500.0));
    }

    @Test
    void create_invalidRequest_returns400() throws Exception {
        RevenueLogDTO dto = new RevenueLogDTO();
        // statId, date, and amount are null

        mockMvc.perform(post("/revenue-logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation"));
    }

    @Test
    void delete_existingId_returns204() throws Exception {
        mockMvc.perform(delete("/revenue-logs/1"))
                .andExpect(status().isNoContent());
    }
}
