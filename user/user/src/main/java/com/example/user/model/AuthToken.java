package com.example.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "auth_tokens")
@Data
@NoArgsConstructor
public class AuthToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;

    @NotNull(message = "Korisnik ne smije biti null.")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Token ne smije biti prazan.")
    @Column(nullable = false)
    private String token;

    @NotNull(message = "Datum isteka tokena ne smije biti null.")
    @Future(message = "Datum isteka tokena mora biti u budućnosti.")
    private LocalDateTime expiresAt;

    public AuthToken(User user, String token, LocalDateTime expiresAt) {
        this.user = user;
        this.token = token;
        this.expiresAt = expiresAt;
    }
}
