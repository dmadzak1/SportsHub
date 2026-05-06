package com.sportshub.facility.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportshub.facility.dto.FacilityDTO;
import com.sportshub.facility.exception.GlobalExceptionHandler;
import com.sportshub.facility.exception.ResourceNotFoundException;
import com.sportshub.facility.model.Facility;
import com.sportshub.facility.service.FacilityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacilityController.class)
@Import({GlobalExceptionHandler.class})
class FacilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacilityService facilityService;

    @Autowired
    private ObjectMapper objectMapper;

    // Uspješan GET /facilities
    @Test
    void getAll_returnsListOfFacilities() throws Exception {
        Facility facility = new Facility("Teren 1", "TENNIS");
        facility.setFacilityId(1L);

        when(facilityService.getAll()).thenReturn(List.of(facility));

        mockMvc.perform(get("/facilities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Teren 1"))
                .andExpect(jsonPath("$[0].type").value("TENNIS"));
    }

    // Neuspješan GET - ne postoji
    @Test
    void getById_notFound_returns404() throws Exception {
        when(facilityService.getById(99L))
                .thenThrow(new ResourceNotFoundException("Facility", 99L));

        mockMvc.perform(get("/facilities/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not_found"));
    }

    // Uspješan POST
    @Test
    void create_validRequest_returns201() throws Exception {
        FacilityDTO dto = new FacilityDTO();
        dto.setName("Bazen A");
        dto.setType("POOL");

        Facility saved = new Facility("Bazen A", "POOL");
        saved.setFacilityId(1L);

        when(facilityService.create(any())).thenReturn(saved);

        mockMvc.perform(post("/facilities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Bazen A"));
    }

    // Neuspješan POST - validacija
    @Test
    void create_invalidRequest_returns400() throws Exception {
        FacilityDTO dto = new FacilityDTO();
        // name i type su prazni

        mockMvc.perform(post("/facilities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation"));
    }
}