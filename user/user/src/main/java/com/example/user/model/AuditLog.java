package com.example.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditLogId;

    @NotNull(message = "Korisnik ne smije biti null.")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Akcija ne smije biti prazna.")
    @Size(max = 100, message = "Akcija ne smije biti duža od 100 znakova.")
    @Column(nullable = false)
    private String action;

    @NotNull(message = "Vrijeme akcije ne smije biti null.")
    @Column(nullable = false)
    private LocalDateTime timestamp;

    public AuditLog(User user, String action, LocalDateTime timestamp) {
        this.user = user;
        this.action = action;
        this.timestamp = timestamp;
    }
}