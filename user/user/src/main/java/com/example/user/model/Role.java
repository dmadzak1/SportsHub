package com.example.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @NotBlank(message = "Naziv uloge ne smije biti prazan.")
    @Size(max = 50, message = "Naziv uloge ne smije biti duži od 50 znakova.")
    @Column(nullable = false, unique = true)
    private String name;

    public Role(String name) {
        this.name = name;
    }
}