package com.example.user.repository;

import com.example.user.model.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {

    Optional<AuthToken> findByToken(String token);

    boolean existsByToken(String token);

    void deleteByToken(String token);
}