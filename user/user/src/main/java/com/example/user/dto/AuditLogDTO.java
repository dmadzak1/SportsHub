package com.example.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuditLogDTO {

    private Long logId;

    @NotNull(message = "ID korisnika ne smije biti null.")
    private Long userId;

    @NotBlank(message = "Akcija ne smije biti prazna.")
    private String action;

    private LocalDateTime timestamp;
}