package com.sportshub.facility.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportshub.facility.config.ModelMapperConfig;
import com.sportshub.facility.dto.FacilityBatchDTO;
import com.sportshub.facility.dto.FacilityDTO;
import com.sportshub.facility.exception.GlobalExceptionHandler;
import com.sportshub.facility.exception.ResourceNotFoundException;
import com.sportshub.facility.model.Facility;
import com.sportshub.facility.service.FacilityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacilityController.class)
@Import({GlobalExceptionHandler.class, ModelMapperConfig.class})
class FacilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FacilityService facilityService;

    @Autowired
    private ObjectMapper objectMapper;

    private Facility mockFacility() {
        Facility f = new Facility("Teren 1", "TENNIS");
        f.setFacilityId(1L);
        return f;
    }

    @Test
    void getAll_returnsListOfFacilities() throws Exception {
        when(facilityService.getAll()).thenReturn(List.of(mockFacility()));

        mockMvc.perform(get("/facilities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Teren 1"));
    }

    @Test
    void getPaginated_returnsPagedFacilities() throws Exception {
        var page = new PageImpl<>(List.of(mockFacility()), PageRequest.of(0, 5), 1);
        when(facilityService.getPaginated(0, 5, "facilityId")).thenReturn(page);

        mockMvc.perform(get("/facilities/paginated?page=0&size=5&sortBy=facilityId"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Teren 1"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getById_existingId_returnsFacility() throws Exception {
        when(facilityService.getById(1L)).thenReturn(mockFacility());

        mockMvc.perform(get("/facilities/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Teren 1"));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(facilityService.getById(99L))
                .thenThrow(new ResourceNotFoundException("Facility", 99L));

        mockMvc.perform(get("/facilities/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not_found"));
    }

    @Test
    void search_returnsMatchingFacilities() throws Exception {
        when(facilityService.searchByName("Teren")).thenReturn(List.of(mockFacility()));

        mockMvc.perform(get("/facilities/search?keyword=Teren"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Teren 1"));
    }

    @Test
    void create_validRequest_returns201() throws Exception {
        when(facilityService.create(any())).thenReturn(mockFacility());

        FacilityDTO dto = new FacilityDTO();
        dto.setName("Teren 1");
        dto.setType("TENNIS");

        mockMvc.perform(post("/facilities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Teren 1"));
    }

    @Test
    void create_invalidRequest_returns400() throws Exception {
        FacilityDTO dto = new FacilityDTO();

        mockMvc.perform(post("/facilities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation"));
    }

    @Test
    void createBatch_validRequest_returns201() throws Exception {
        when(facilityService.createBatch(any())).thenReturn(List.of(mockFacility()));

        FacilityDTO dto = new FacilityDTO();
        dto.setName("Teren 1");
        dto.setType("TENNIS");

        FacilityBatchDTO batchDTO = new FacilityBatchDTO();
        batchDTO.setFacilities(List.of(dto));

        mockMvc.perform(post("/facilities/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batchDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].name").value("Teren 1"));
    }

    @Test
    void createBatch_emptyList_returns400() throws Exception {
        FacilityBatchDTO batchDTO = new FacilityBatchDTO();
        batchDTO.setFacilities(List.of());

        mockMvc.perform(post("/facilities/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batchDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation"));
    }

    @Test
    void patch_validRequest_returnsUpdatedFacility() throws Exception {
        Facility updated = new Facility("Novi naziv", "TENNIS");
        updated.setFacilityId(1L);

        when(facilityService.getById(1L)).thenReturn(mockFacility());
        when(facilityService.update(eq(1L), any())).thenReturn(updated);

        FacilityDTO dto = new FacilityDTO();
        dto.setName("Novi naziv");

        mockMvc.perform(patch("/facilities/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Novi naziv"));
    }

    @Test
    void delete_existingId_returns204() throws Exception {
        mockMvc.perform(delete("/facilities/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_notFound_returns404() throws Exception {
        org.mockito.Mockito.doThrow(new ResourceNotFoundException("Facility", 99L))
                .when(facilityService).delete(99L);

        mockMvc.perform(delete("/facilities/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not_found"));
    }
}
