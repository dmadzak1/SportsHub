package com.example.user.controller;

import com.example.user.model.Role;
import com.example.user.repository.RoleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleRepository roleRepository;

    public RoleController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // GET /roles
    @GetMapping
    public List<Role> getAll() {
        return roleRepository.findAll();
    }

    // GET /roles/1
    @GetMapping("/{id}")
    public ResponseEntity<Role> getById(@PathVariable Long id) {
        return roleRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /roles
    @PostMapping
    public Role create(@RequestBody Role role) {
        return roleRepository.save(role);
    }
}