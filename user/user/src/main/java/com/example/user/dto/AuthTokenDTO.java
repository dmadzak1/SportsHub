package com.example.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuthTokenDTO {

    private Long tokenId;

    @NotNull(message = "ID korisnika ne smije biti null.")
    private Long userId;

    @NotBlank(message = "Token ne smije biti prazan.")
    private String token;

    private LocalDateTime expiresAt;
}