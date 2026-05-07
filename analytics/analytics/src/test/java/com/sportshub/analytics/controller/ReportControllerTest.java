package com.sportshub.analytics.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportshub.analytics.config.AppConfig;
import com.sportshub.analytics.dto.ReportDTO;
import com.sportshub.analytics.exception.GlobalExceptionHandler;
import com.sportshub.analytics.exception.ResourceNotFoundException;
import com.sportshub.analytics.model.Report;
import com.sportshub.analytics.service.ReportService;
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

@WebMvcTest(ReportController.class)
@Import({GlobalExceptionHandler.class, AppConfig.class})
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportService reportService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAll_returnsListOfReports() throws Exception {
        Report report = new Report("REVENUE");
        report.setReportId(1L);
        when(reportService.getAll()).thenReturn(List.of(report));

        mockMvc.perform(get("/reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reportType").value("REVENUE"));
    }

    @Test
    void getById_existingId_returnsReport() throws Exception {
        Report report = new Report("CAPACITY");
        report.setReportId(1L);
        when(reportService.getById(1L)).thenReturn(report);

        mockMvc.perform(get("/reports/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reportType").value("CAPACITY"));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(reportService.getById(99L))
                .thenThrow(new ResourceNotFoundException("Report", 99L));

        mockMvc.perform(get("/reports/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not_found"));
    }

    @Test
    void create_validRequest_returns201() throws Exception {
        ReportDTO dto = new ReportDTO();
        dto.setReportType("REVENUE");

        Report saved = new Report("REVENUE");
        saved.setReportId(1L);
        when(reportService.create(any())).thenReturn(saved);

        mockMvc.perform(post("/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reportType").value("REVENUE"));
    }

    @Test
    void create_invalidRequest_returns400() throws Exception {
        ReportDTO dto = new ReportDTO();
        // reportType is blank

        mockMvc.perform(post("/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation"));
    }

    @Test
    void delete_existingId_returns204() throws Exception {
        mockMvc.perform(delete("/reports/1"))
                .andExpect(status().isNoContent());
    }
}
