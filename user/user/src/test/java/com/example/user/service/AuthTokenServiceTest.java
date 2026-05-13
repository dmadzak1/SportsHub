package com.example.user.service;

import com.example.user.exception.ResourceNotFoundException;
import com.example.user.model.AuthToken;
import com.example.user.model.Role;
import com.example.user.model.User;
import com.example.user.repository.AuthTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthTokenServiceTest {

    @Mock
    private AuthTokenRepository authTokenRepository;

    @InjectMocks
    private AuthTokenService authTokenService;

    private AuthToken authToken;
    private User user;

    @BeforeEach
    void setUp() {
        Role role = new Role("USER");
        role.setRoleId(1L);

        user = new User("ana@email.com", "pass", role);
        user.setUserId(1L);

        authToken = new AuthToken(user, "token-abc-123", LocalDateTime.now().plusHours(2));
        authToken.setTokenId(1L);
    }

    @Test
    void getAll_returnsAllTokens() {
        when(authTokenRepository.findAll()).thenReturn(List.of(authToken));

        List<AuthToken> result = authTokenService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getToken()).isEqualTo("token-abc-123");
    }

    @Test
    void getById_existingId_returnsToken() {
        when(authTokenRepository.findById(1L)).thenReturn(Optional.of(authToken));

        AuthToken result = authTokenService.getById(1L);

        assertThat(result.getToken()).isEqualTo("token-abc-123");
    }

    @Test
    void getById_nonExistingId_throwsException() {
        when(authTokenRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authTokenService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_savesToken() {
        when(authTokenRepository.save(authToken)).thenReturn(authToken);

        AuthToken result = authTokenService.create(authToken);

        assertThat(result.getToken()).isEqualTo("token-abc-123");
        verify(authTokenRepository, times(1)).save(authToken);
    }

    @Test
    void delete_existingId_deletesSuccessfully() {
        when(authTokenRepository.existsById(1L)).thenReturn(true);

        authTokenService.delete(1L);

        verify(authTokenRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(authTokenRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> authTokenService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}