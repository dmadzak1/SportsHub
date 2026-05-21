package com.example.user.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.user.model.Role;
import com.example.user.repository.AuditLogRepository;
import com.example.user.repository.AuthTokenRepository;
import com.example.user.repository.RoleRepository;
import com.example.user.repository.UserRepository;
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
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthTokenRepository authTokenRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        authTokenRepository.deleteAll();
        auditLogRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void createUser_thenFetchById() throws Exception {
        Role role = roleRepository.save(new Role("USER"));

        MvcResult createResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", "integration.user@sportshub.com",
                                "password", "pass123",
                                "roleId", role.getRoleId()
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").isNumber())
                .andExpect(jsonPath("$.email").value("integration.user@sportshub.com"))
                .andReturn();

        JsonNode created = objectMapper.readTree(createResult.getResponse().getContentAsString());
        long userId = created.get("userId").asLong();

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value((int) userId))
                .andExpect(jsonPath("$.roleId").value(role.getRoleId().intValue()));
    }

    @Test
    void createUser_thenSearchByEmail() throws Exception {
        Role role = roleRepository.save(new Role("ADMIN"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", "admin.integration@sportshub.com",
                                "password", "secure",
                                "roleId", role.getRoleId()
                        ))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/users/search").param("keyword", "integration"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("admin.integration@sportshub.com"));
    }
}
