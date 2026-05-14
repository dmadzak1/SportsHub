package com.example.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank(message = "Email ne smije biti prazan.")
    @Email(message = "Email mora biti ispravnog formata.")
    @Size(max = 100, message = "Email ne smije biti duži od 100 znakova.")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Lozinka ne smije biti prazna.")
    @Size(min = 6, max = 100, message = "Lozinka mora imati između 6 i 100 znakova.")
    @Column(nullable = false)
    private String password;

    @NotNull(message = "Uloga ne smije biti null.")
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<AuthToken> authTokens = new ArrayList<>();

    public User(String email, String password, Role role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }
}