package com.example.user.controller;

import com.example.user.dto.LoginRequestDTO;
import com.example.user.dto.LoginResponseDTO;
import com.example.user.model.AuthToken;
import com.example.user.model.User;
import com.example.user.security.JwtService;
import com.example.user.service.AuthTokenService;
import com.example.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthTokenService authTokenService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            UserService userService,
            AuthTokenService authTokenService,
            JwtService jwtService,
            PasswordEncoder passwordEncoder
    ) {
        this.userService = userService;
        this.authTokenService = authTokenService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        User user = userService.getByEmail(request.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtService.generateToken(user);

        AuthToken authToken = new AuthToken(
                user,
                token,
                LocalDateTime.now().plusSeconds(jwtService.getExpirationSeconds())
        );

        authTokenService.create(authToken);

        return ResponseEntity.ok(new LoginResponseDTO(
                token,
                "Bearer",
                user.getUserId(),
                user.getEmail(),
                user.getRole().getName(),
                jwtService.getExpirationSeconds()
        ));
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        authTokenService.deleteByToken(token);
    }
}