package com.example.user.service;

import com.example.user.exception.ResourceNotFoundException;
import com.example.user.model.Role;
import com.example.user.model.User;
import com.example.user.repository.UserRepository;
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
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        Role role = new Role("USER");
        role.setRoleId(1L);
        user = new User("ana@email.com", "hashed_pass", role);
        user.setUserId(1L);
    }

    @Test
    void getAll_returnsAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> result = userService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("ana@email.com");
    }

    @Test
    void getById_existingId_returnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getById(1L);

        assertThat(result.getEmail()).isEqualTo("ana@email.com");
    }

    @Test
    void getById_nonExistingId_throwsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_savesUser() {
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.create(user);

        assertThat(result.getEmail()).isEqualTo("ana@email.com");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void delete_existingId_deletesSuccessfully() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.delete(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> userService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}