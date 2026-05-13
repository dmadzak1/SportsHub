package com.example.user.service;

import com.example.user.exception.ResourceNotFoundException;
import com.example.user.model.Role;
import com.example.user.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role("ADMIN");
        role.setRoleId(1L);
    }

    @Test
    void getAll_returnsAllRoles() {
        when(roleRepository.findAll()).thenReturn(List.of(role));

        List<Role> result = roleService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRoleName()).isEqualTo("ADMIN");
    }

    @Test
    void getById_existingId_returnsRole() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        Role result = roleService.getById(1L);

        assertThat(result.getRoleName()).isEqualTo("ADMIN");
    }

    @Test
    void getById_nonExistingId_throwsException() {
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_savesRole() {
        when(roleRepository.save(role)).thenReturn(role);

        Role result = roleService.create(role);

        assertThat(result.getRoleName()).isEqualTo("ADMIN");
        verify(roleRepository, times(1)).save(role);
    }

    @Test
    void update_existingId_updatesRole() {
        Role updated = new Role("USER");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleRepository.save(any())).thenReturn(updated);

        Role result = roleService.update(1L, updated);

        assertThat(result.getRoleName()).isEqualTo("USER");
    }

    @Test
    void update_nonExistingId_throwsException() {
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.update(99L, role))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existingId_deletesSuccessfully() {
        when(roleRepository.existsById(1L)).thenReturn(true);

        roleService.delete(1L);

        verify(roleRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(roleRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> roleService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}