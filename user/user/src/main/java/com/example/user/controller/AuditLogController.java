package com.example.user.controller;

import com.example.user.dto.AuditLogDTO;
import com.example.user.model.AuditLog;
import com.example.user.model.User;
import com.example.user.service.AuditLogService;
import com.example.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;
    private final UserService userService;

    public AuditLogController(AuditLogService auditLogService, UserService userService) {
        this.auditLogService = auditLogService;
        this.userService = userService;
    }

    @GetMapping
    public List<AuditLogDTO> getAll() {
        return auditLogService.getAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public AuditLogDTO getById(@PathVariable Long id) {
        return toDTO(auditLogService.getById(id));
    }

    @PostMapping
    public ResponseEntity<AuditLogDTO> create(@Valid @RequestBody AuditLogDTO dto) {
        User user = userService.getById(dto.getUserId());
        AuditLog auditLog = new AuditLog(user, dto.getAction());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toDTO(auditLogService.create(auditLog)));
    }

    private AuditLogDTO toDTO(AuditLog log) {
        AuditLogDTO dto = new AuditLogDTO();
        dto.setLogId(log.getLogId());
        dto.setUserId(log.getUser().getUserId());
        dto.setAction(log.getAction());
        dto.setTimestamp(log.getTimestamp());
        return dto;
    }
}