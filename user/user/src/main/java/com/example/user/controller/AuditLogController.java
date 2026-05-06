package com.example.user.controller;

import com.example.user.model.AuditLog;
import com.example.user.repository.AuditLogRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/audit-logs")
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    public AuditLogController(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    // GET /audit-logs
    @GetMapping
    public List<AuditLog> getAll() {
        return auditLogRepository.findAll();
    }

    // GET /audit-logs/1
    @GetMapping("/{id}")
    public ResponseEntity<AuditLog> getById(@PathVariable Long id) {
        return auditLogRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /audit-logs
    @PostMapping
    public AuditLog create(@RequestBody AuditLog auditLog) {
        return auditLogRepository.save(auditLog);
    }
}