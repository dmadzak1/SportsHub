package com.example.user.controller;

import com.example.user.dto.UserDTO;
import com.example.user.model.Role;
import com.example.user.model.User;
import com.example.user.service.RoleService;
import com.example.user.service.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
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

    @GetMapping
    public List<UserDTO> getAll() {
        return userService.getAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public UserDTO getById(@PathVariable Long id) {
        return toDTO(userService.getById(id));
    }

    @GetMapping("/email/{email}")
    public UserDTO getByEmail(@PathVariable String email) {
        return toDTO(userService.getByEmail(email));
    }

    @PostMapping
    public ResponseEntity<UserDTO> create(@Valid @RequestBody UserDTO dto) {
        Role role = roleService.getById(dto.getRoleId());
        User user = new User(dto.getEmail(), dto.getPassword(), role);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toDTO(userService.create(user)));
    }

    @PutMapping("/{id}")
    public UserDTO update(@PathVariable Long id, @Valid @RequestBody UserDTO dto) {
        Role role = roleService.getById(dto.getRoleId());
        User user = new User(dto.getEmail(), dto.getPassword(), role);
        return toDTO(userService.update(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
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