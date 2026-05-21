package com.sportshub.facility.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportshub.facility.repository.FacilityRepository;
import com.sportshub.facility.repository.ReservationRepository;
import com.sportshub.facility.repository.ScheduleRepository;
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
class FacilityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    @BeforeEach
    void setUp() {
        reservationRepository.deleteAll();
        scheduleRepository.deleteAll();
        facilityRepository.deleteAll();
    }

    @Test
    void createFacility_thenFetchById() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/facilities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Integracijski teren",
                                "type", "TENNIS"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.facilityId").isNumber())
                .andExpect(jsonPath("$.name").value("Integracijski teren"))
                .andReturn();

        JsonNode created = objectMapper.readTree(createResult.getResponse().getContentAsString());
        long facilityId = created.get("facilityId").asLong();

        mockMvc.perform(get("/facilities/{id}", facilityId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.facilityId").value((int) facilityId))
                .andExpect(jsonPath("$.type").value("TENNIS"));
    }

    @Test
    void createFacility_thenFilterByType() throws Exception {
        mockMvc.perform(post("/facilities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Test bazen",
                                "type", "POOL"
                        ))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/facilities/type/POOL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("POOL"));
    }
}
