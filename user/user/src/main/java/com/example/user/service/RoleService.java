package com.example.user.service;

import com.example.user.exception.ResourceNotFoundException;
import com.example.user.model.Role;
import com.example.user.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getAll() {
        return roleRepository.findAll();
    }

    public Role getById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));
    }

    public Role create(Role role) {
        return roleRepository.save(role);
    }

    public Role update(Long id, Role updated) {
        Role existing = getById(id);
        existing.setRoleName(updated.getRoleName());
        return roleRepository.save(existing);
    }

    public void delete(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role", id);
        }
        roleRepository.deleteById(id);
    }
}