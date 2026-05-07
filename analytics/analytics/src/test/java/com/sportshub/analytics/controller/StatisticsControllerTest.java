package com.sportshub.analytics.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportshub.analytics.config.AppConfig;
import com.sportshub.analytics.dto.StatisticsDTO;
import com.sportshub.analytics.exception.GlobalExceptionHandler;
import com.sportshub.analytics.exception.ResourceNotFoundException;
import com.sportshub.analytics.model.Report;
import com.sportshub.analytics.model.Statistics;
import com.sportshub.analytics.service.ReportService;
import com.sportshub.analytics.service.StatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatisticsController.class)
@Import({GlobalExceptionHandler.class, AppConfig.class})
class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StatisticsService statisticsService;

    @MockitoBean
    private ReportService reportService;

    @Autowired
    private ObjectMapper objectMapper;

    private Report report;
    private Statistics stat;

    @BeforeEach
    void setUp() {
        report = new Report("REVENUE");
        report.setReportId(1L);
        stat = new Statistics(report, "TOTAL_REVENUE", 1000.0);
        stat.setStatId(1L);
    }

    @Test
    void getAll_returnsListOfStatistics() throws Exception {
        when(statisticsService.getAll()).thenReturn(List.of(stat));

        mockMvc.perform(get("/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].metric").value("TOTAL_REVENUE"))
                .andExpect(jsonPath("$[0].reportId").value(1));
    }

    @Test
    void getById_existingId_returnsStatistics() throws Exception {
        when(statisticsService.getById(1L)).thenReturn(stat);

        mockMvc.perform(get("/statistics/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metric").value("TOTAL_REVENUE"))
                .andExpect(jsonPath("$.value").value(1000.0));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(statisticsService.getById(99L))
                .thenThrow(new ResourceNotFoundException("Statistics", 99L));

        mockMvc.perform(get("/statistics/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not_found"));
    }

    @Test
    void create_validRequest_returns201() throws Exception {
        StatisticsDTO dto = new StatisticsDTO();
        dto.setReportId(1L);
        dto.setMetric("TOTAL_REVENUE");
        dto.setValue(1000.0);

        when(reportService.getById(1L)).thenReturn(report);
        when(statisticsService.create(any())).thenReturn(stat);

        mockMvc.perform(post("/statistics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.metric").value("TOTAL_REVENUE"));
    }

    @Test
    void create_invalidRequest_returns400() throws Exception {
        StatisticsDTO dto = new StatisticsDTO();
        // reportId and value are null, metric is blank

        mockMvc.perform(post("/statistics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation"));
    }

    @Test
    void delete_existingId_returns204() throws Exception {
        mockMvc.perform(delete("/statistics/1"))
                .andExpect(status().isNoContent());
    }
}
