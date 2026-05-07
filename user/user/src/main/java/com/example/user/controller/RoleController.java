package com.example.user.controller;

import com.example.user.dto.RoleDTO;
import com.example.user.model.Role;
import com.example.user.service.RoleService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;
    private final ModelMapper modelMapper;

    public RoleController(RoleService roleService, ModelMapper modelMapper) {
        this.roleService = roleService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<RoleDTO> getAll() {
        return roleService.getAll().stream()
                .map(r -> modelMapper.map(r, RoleDTO.class))
                .toList();
    }

    @GetMapping("/{id}")
    public RoleDTO getById(@PathVariable Long id) {
        return modelMapper.map(roleService.getById(id), RoleDTO.class);
    }

    @PostMapping
    public ResponseEntity<RoleDTO> create(@Valid @RequestBody RoleDTO dto) {
        Role created = roleService.create(modelMapper.map(dto, Role.class));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(modelMapper.map(created, RoleDTO.class));
    }

    @PutMapping("/{id}")
    public RoleDTO update(@PathVariable Long id, @Valid @RequestBody RoleDTO dto) {
        Role updated = roleService.update(id, modelMapper.map(dto, Role.class));
        return modelMapper.map(updated, RoleDTO.class);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}