package com.example.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data
public class LoginResponseDTO {
    private String token;
    public String TokenType;
    private Long userId;
    private String email;
    private String role;
    private Long expiresInSeconds;

    public LoginResponseDTO(String token, String bearer, Long userId, @NotBlank(message = "Email ne smije biti prazan.") @Email(message = "Email mora biti ispravnog formata.") @Size(max = 100, message = "Email ne smije biti duži od 100 znakova.") String email, @NotBlank(message = "Naziv uloge ne smije biti prazan.") @Size(max = 50, message = "Naziv uloge ne smije biti duži od 50 znakova.") String name, long expirationSeconds) {
    this.token=token;
    this.TokenType=bearer;
    this.userId=userId;
    this.email=email;
    this.role=name;
    this.expiresInSeconds=expirationSeconds;
    }
}
