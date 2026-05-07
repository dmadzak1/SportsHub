package com.example.user.service;

import com.example.user.exception.ResourceNotFoundException;
import com.example.user.model.AuthToken;
import com.example.user.repository.AuthTokenRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthTokenService {

    private final AuthTokenRepository authTokenRepository;

    public AuthTokenService(AuthTokenRepository authTokenRepository) {
        this.authTokenRepository = authTokenRepository;
    }

    public List<AuthToken> getAll() {
        return authTokenRepository.findAll();
    }

    public AuthToken getById(Long id) {
        return authTokenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AuthToken", id));
    }

    public AuthToken create(AuthToken authToken) {
        return authTokenRepository.save(authToken);
    }

    public void delete(Long id) {
        if (!authTokenRepository.existsById(id)) {
            throw new ResourceNotFoundException("AuthToken", id);
        }
        authTokenRepository.deleteById(id);
    }
}