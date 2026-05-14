package com.example.user.controller;

import com.example.user.dto.PageResponseDTO;
import com.example.user.dto.UserBatchDTO;
import com.example.user.dto.UserDTO;
import com.example.user.dto.UserWithPromotionsDTO;
import com.example.user.model.Role;
import com.example.user.model.User;
import com.example.user.service.RoleService;
import com.example.user.service.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final ModelMapper modelMapper;

    public UserController(UserService userService, RoleService roleService, ModelMapper modelMapper) {
        this.userService = userService;
        this.roleService = roleService;
        this.modelMapper = modelMapper;
    }

    // GET /users
    @GetMapping
    public List<UserDTO> getAll() {
        return userService.getAll().stream()
                .map(this::toDTO)
                .toList();
    }

    // GET /users/paginated?page=0&size=5&sortBy=email
    @GetMapping("/paginated")
    public PageResponseDTO<UserDTO> getPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "userId") String sortBy) {

        Page<User> result = userService.getPaginated(page, size, sortBy);
        List<UserDTO> content = result.getContent().stream()
                .map(this::toDTO)
                .toList();
        return new PageResponseDTO<>(content, page, size,
                result.getTotalElements(), result.getTotalPages());
    }

    // GET /users/{id}
    @GetMapping("/{id}")
    public UserDTO getById(@PathVariable Long id) {
        return toDTO(userService.getById(id));
    }

    // GET /users/email/{email}
    @GetMapping("/email/{email}")
    public UserDTO getByEmail(@PathVariable String email) {
        return toDTO(userService.getByEmail(email));
    }

    // GET /users/role/USER
    @GetMapping("/role/{roleName}")
    public List<UserDTO> getByRole(@PathVariable String roleName) {
        return userService.getByRole(roleName).stream()
                .map(this::toDTO)
                .toList();
    }

    // GET /users/search?keyword=ana
    @GetMapping("/search")
    public List<UserDTO> search(@RequestParam String keyword) {
        return userService.searchByEmail(keyword).stream()
                .map(this::toDTO)
                .toList();
    }

    // GET /users/with-tokens
    @GetMapping("/with-tokens")
    public List<UserDTO> getUsersWithActiveTokens() {
        return userService.getUsersWithActiveTokens().stream()
                .map(this::toDTO)
                .toList();
    }

    // POST /users
    @PostMapping
    public ResponseEntity<UserDTO> create(@Valid @RequestBody UserDTO dto) {
        Role role = roleService.getById(dto.getRoleId());
        User user = new User(dto.getEmail(), dto.getPassword(), role);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toDTO(userService.create(user)));
    }

    // POST /users/with-audit
    @PostMapping("/with-audit")
    public ResponseEntity<UserDTO> createWithAudit(@Valid @RequestBody UserDTO dto) {
        Role role = roleService.getById(dto.getRoleId());
        User user = new User(dto.getEmail(), dto.getPassword(), role);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toDTO(userService.createWithAuditLog(user)));
    }

    // POST /users/batch
    @PostMapping("/batch")
    public ResponseEntity<List<UserDTO>> createBatch(@Valid @RequestBody UserBatchDTO batchDTO) {
        List<User> users = batchDTO.getUsers().stream()
                .map(dto -> {
                    Role role = roleService.getById(dto.getRoleId());
                    return new User(dto.getEmail(), dto.getPassword(), role);
                })
                .toList();

        List<UserDTO> created = userService.createBatch(users).stream()
                .map(this::toDTO)
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /users/{id}
    @PutMapping("/{id}")
    public UserDTO update(@PathVariable Long id, @Valid @RequestBody UserDTO dto) {
        Role role = roleService.getById(dto.getRoleId());
        User user = new User(dto.getEmail(), dto.getPassword(), role);
        return toDTO(userService.update(id, user));
    }

    // PATCH /users/{id}
    @PatchMapping("/{id}")
    public UserDTO patch(@PathVariable Long id, @RequestBody UserDTO dto) {
        User existing = userService.getById(id);
        if (dto.getEmail() != null) existing.setEmail(dto.getEmail());
        if (dto.getPassword() != null) existing.setPassword(dto.getPassword());
        if (dto.getRoleId() != null) existing.setRole(roleService.getById(dto.getRoleId()));
        return toDTO(userService.update(id, existing));
    }

    // DELETE /users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // GET /users/{id}/promotions
    @GetMapping("/{id}/promotions")
    public UserWithPromotionsDTO getUserWithPromotions(@PathVariable Long id) {
        return userService.getUserWithPromotions(id);
    }

    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        dto.setRoleId(user.getRole() != null ? user.getRole().getRoleId() : null);
        return dto;
    }
}