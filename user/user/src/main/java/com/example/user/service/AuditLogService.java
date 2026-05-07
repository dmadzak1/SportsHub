package com.example.user.service;

import com.example.user.exception.ResourceNotFoundException;
import com.example.user.model.AuditLog;
import com.example.user.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public List<AuditLog> getAll() {
        return auditLogRepository.findAll();
    }

    public AuditLog getById(Long id) {
        return auditLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AuditLog", id));
    }

    public AuditLog create(AuditLog auditLog) {
        return auditLogRepository.save(auditLog);
    }
}