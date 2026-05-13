package com.example.user.service;

import com.example.user.exception.ResourceNotFoundException;
import com.example.user.model.AuditLog;
import com.example.user.model.Role;
import com.example.user.model.User;
import com.example.user.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogService auditLogService;

    private AuditLog auditLog;
    private User user;

    @BeforeEach
    void setUp() {
        Role role = new Role("USER");
        role.setRoleId(1L);

        user = new User("ana@email.com", "pass", role);
        user.setUserId(1L);

        auditLog = new AuditLog(user, "LOGIN");
        auditLog.setLogId(1L);
    }

    @Test
    void getAll_returnsAllLogs() {
        when(auditLogRepository.findAll()).thenReturn(List.of(auditLog));

        List<AuditLog> result = auditLogService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAction()).isEqualTo("LOGIN");
    }

    @Test
    void getById_existingId_returnsLog() {
        when(auditLogRepository.findById(1L)).thenReturn(Optional.of(auditLog));

        AuditLog result = auditLogService.getById(1L);

        assertThat(result.getAction()).isEqualTo("LOGIN");
    }

    @Test
    void getById_nonExistingId_throwsException() {
        when(auditLogRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> auditLogService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_savesLog() {
        when(auditLogRepository.save(auditLog)).thenReturn(auditLog);

        AuditLog result = auditLogService.create(auditLog);

        assertThat(result.getAction()).isEqualTo("LOGIN");
        verify(auditLogRepository, times(1)).save(auditLog);
    }
}