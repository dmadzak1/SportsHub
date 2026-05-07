package com.example.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.user.dto.UserDTO;
import com.example.user.exception.GlobalExceptionHandler;
import com.example.user.exception.ResourceNotFoundException;
import com.example.user.model.Role;
import com.example.user.model.User;
import com.example.user.service.RoleService;
import com.example.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private RoleService roleService;

    @Autowired
    private ObjectMapper objectMapper;

    // Uspješan GET /users
    @Test
    void getAll_returnsListOfUsers() throws Exception {
        Role role = new Role("USER");
        role.setRoleId(1L);
        User user = new User("ana@email.com", "pass", role);
        user.setUserId(1L);

        when(userService.getAll()).thenReturn(List.of(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("ana@email.com"))
                .andExpect(jsonPath("$[0].roleId").value(1));
    }

    // Neuspješan GET /users/{id} - ne postoji
    @Test
    void getById_notFound_returns404() throws Exception {
        when(userService.getById(99L))
                .thenThrow(new ResourceNotFoundException("User", 99L));

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not_found"));
    }

    // Uspješan POST /users
    @Test
    void create_validRequest_returns201() throws Exception {
        Role role = new Role("USER");
        role.setRoleId(1L);
        User saved = new User("novi@email.com", "pass123", role);
        saved.setUserId(2L);

        when(roleService.getById(1L)).thenReturn(role);
        when(userService.create(any())).thenReturn(saved);

        UserDTO dto = new UserDTO();
        dto.setEmail("novi@email.com");
        dto.setPassword("pass123");
        dto.setRoleId(1L);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("novi@email.com"));
    }

    // Neuspješan POST /users - neispravan email
    @Test
    void create_invalidEmail_returns400() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setEmail("ovo-nije-email");
        dto.setPassword("pass123");
        dto.setRoleId(1L);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation"))
                .andExpect(jsonPath("$.messages.email").exists());
    }

    // Neuspješan POST /users - prazan body
    @Test
    void create_emptyBody_returns400() throws Exception {
        UserDTO dto = new UserDTO();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation"));
    }
}