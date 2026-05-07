package com.example.user.controller;

import com.example.user.dto.AuthTokenDTO;
import com.example.user.model.AuthToken;
import com.example.user.model.User;
import com.example.user.service.AuthTokenService;
import com.example.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth-tokens")
public class AuthTokenController {

    private final AuthTokenService authTokenService;
    private final UserService userService;

    public AuthTokenController(AuthTokenService authTokenService, UserService userService) {
        this.authTokenService = authTokenService;
        this.userService = userService;
    }

    @GetMapping
    public List<AuthTokenDTO> getAll() {
        return authTokenService.getAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public AuthTokenDTO getById(@PathVariable Long id) {
        return toDTO(authTokenService.getById(id));
    }

    @PostMapping
    public ResponseEntity<AuthTokenDTO> create(@Valid @RequestBody AuthTokenDTO dto) {
        User user = userService.getById(dto.getUserId());
        AuthToken token = new AuthToken(user, dto.getToken(), dto.getExpiresAt());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toDTO(authTokenService.create(token)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        authTokenService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private AuthTokenDTO toDTO(AuthToken token) {
        AuthTokenDTO dto = new AuthTokenDTO();
        dto.setTokenId(token.getTokenId());
        dto.setUserId(token.getUser().getUserId());
        dto.setToken(token.getToken());
        dto.setExpiresAt(token.getExpiresAt());
        return dto;
    }
}