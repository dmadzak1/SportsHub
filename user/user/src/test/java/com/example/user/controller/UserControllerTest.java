package com.example.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.user.dto.UserBatchDTO;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private RoleService roleService;

    @Autowired
    private ObjectMapper objectMapper;

    private Role mockRole() {
        Role role = new Role("USER");
        role.setRoleId(1L);
        return role;
    }

    private User mockUser() {
        User user = new User("ana@email.com", "pass", mockRole());
        user.setUserId(1L);
        return user;
    }

    // GET /users
    @Test
    void getAll_returnsListOfUsers() throws Exception {
        when(userService.getAll()).thenReturn(List.of(mockUser()));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("ana@email.com"));
    }

    // GET /users/paginated
    @Test
    void getPaginated_returnsPagedUsers() throws Exception {
        var page = new PageImpl<>(List.of(mockUser()), PageRequest.of(0, 5), 1);
        when(userService.getPaginated(0, 5, "userId")).thenReturn(page);

        mockMvc.perform(get("/users/paginated?page=0&size=5&sortBy=userId"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email").value("ana@email.com"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    // GET /users/{id} — uspješan
    @Test
    void getById_existingId_returnsUser() throws Exception {
        when(userService.getById(1L)).thenReturn(mockUser());

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ana@email.com"));
    }

    // GET /users/{id} — neuspješan
    @Test
    void getById_notFound_returns404() throws Exception {
        when(userService.getById(99L))
                .thenThrow(new ResourceNotFoundException("User", 99L));

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not_found"));
    }

    // GET /users/role/{roleName}
    @Test
    void getByRole_returnsUsers() throws Exception {
        when(userService.getByRole("USER")).thenReturn(List.of(mockUser()));

        mockMvc.perform(get("/users/role/USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("ana@email.com"));
    }

    // GET /users/search
    @Test
    void search_returnsMatchingUsers() throws Exception {
        when(userService.searchByEmail("ana")).thenReturn(List.of(mockUser()));

        mockMvc.perform(get("/users/search?keyword=ana"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("ana@email.com"));
    }

    // GET /users/with-tokens
    @Test
    void getUsersWithActiveTokens_returnsList() throws Exception {
        when(userService.getUsersWithActiveTokens()).thenReturn(List.of(mockUser()));

        mockMvc.perform(get("/users/with-tokens"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("ana@email.com"));
    }

    // POST /users — uspješan
    @Test
    void create_validRequest_returns201() throws Exception {
        when(roleService.getById(1L)).thenReturn(mockRole());
        when(userService.create(any())).thenReturn(mockUser());

        UserDTO dto = new UserDTO();
        dto.setEmail("ana@email.com");
        dto.setPassword("pass123");
        dto.setRoleId(1L);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("ana@email.com"));
    }

    // POST /users — neispravan email
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
                .andExpect(jsonPath("$.error").value("validation"));
    }

    // POST /users/with-audit
    @Test
    void createWithAudit_validRequest_returns201() throws Exception {
        when(roleService.getById(1L)).thenReturn(mockRole());
        when(userService.createWithAuditLog(any())).thenReturn(mockUser());

        UserDTO dto = new UserDTO();
        dto.setEmail("ana@email.com");
        dto.setPassword("pass123");
        dto.setRoleId(1L);

        mockMvc.perform(post("/users/with-audit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("ana@email.com"));
    }

    // POST /users/batch — uspješan
    @Test
    void createBatch_validRequest_returns201() throws Exception {
        when(roleService.getById(1L)).thenReturn(mockRole());
        when(userService.createBatch(any())).thenReturn(List.of(mockUser()));

        UserDTO dto = new UserDTO();
        dto.setEmail("ana@email.com");
        dto.setPassword("pass123");
        dto.setRoleId(1L);

        UserBatchDTO batchDTO = new UserBatchDTO();
        batchDTO.setUsers(List.of(dto));

        mockMvc.perform(post("/users/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batchDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].email").value("ana@email.com"));
    }

    // POST /users/batch — prazna lista
    @Test
    void createBatch_emptyList_returns400() throws Exception {
        UserBatchDTO batchDTO = new UserBatchDTO();
        batchDTO.setUsers(List.of());

        mockMvc.perform(post("/users/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batchDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation"));
    }

    // PATCH /users/{id}
    @Test
    void patch_validRequest_returnsUpdatedUser() throws Exception {
        User updated = new User("novi@email.com", "pass", mockRole());
        updated.setUserId(1L);

        when(userService.getById(1L)).thenReturn(mockUser());
        when(userService.update(eq(1L), any())).thenReturn(updated);

        UserDTO dto = new UserDTO();
        dto.setEmail("novi@email.com");

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("novi@email.com"));
    }

    // DELETE /users/{id} — uspješan
    @Test
    void delete_existingId_returns204() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }

    // DELETE /users/{id} — ne postoji
    @Test
    void delete_notFound_returns404() throws Exception {
        org.mockito.Mockito.doThrow(new ResourceNotFoundException("User", 99L))
                .when(userService).delete(99L);

        mockMvc.perform(delete("/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not_found"));
    }
}