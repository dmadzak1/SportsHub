package com.example.user.controller;

import com.example.user.model.AuthToken;
import com.example.user.repository.AuthTokenRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth-tokens")
public class AuthTokenController {

    private final AuthTokenRepository authTokenRepository;

    public AuthTokenController(AuthTokenRepository authTokenRepository) {
        this.authTokenRepository = authTokenRepository;
    }

    // GET /auth-tokens
    @GetMapping
    public List<AuthToken> getAll() {
        return authTokenRepository.findAll();
    }

    // GET /auth-tokens/1
    @GetMapping("/{id}")
    public ResponseEntity<AuthToken> getById(@PathVariable Long id) {
        return authTokenRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /auth-tokens
    @PostMapping
    public AuthToken create(@RequestBody AuthToken authToken) {
        return authTokenRepository.save(authToken);
    }

    // DELETE /auth-tokens/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!authTokenRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        authTokenRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}