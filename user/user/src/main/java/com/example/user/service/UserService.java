package com.example.user.service;

import com.example.user.exception.ResourceNotFoundException;
import com.example.user.model.AuditLog;
import com.example.user.model.User;
import com.example.user.repository.AuditLogRepository;
import com.example.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    public UserService(UserRepository userRepository, AuditLogRepository auditLogRepository) {
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User sa emailom " + email + " nije pronađen.", 0L));
    }

    public User create(User user) {
        return userRepository.save(user);
    }

    public User update(Long id, User updated) {
        User existing = getById(id);
        existing.setEmail(updated.getEmail());
        existing.setPassword(updated.getPassword());
        return userRepository.save(existing);
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", id);
        }
        userRepository.deleteById(id);
    }

    // Paginacija i sortiranje
    public Page<User> getPaginated(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return userRepository.findAll(pageable);
    }

    // Custom — po ulozi
    public List<User> getByRole(String roleName) {
        return userRepository.findByRoleRoleName(roleName);
    }

    // Custom — pretraga po emailu
    public List<User> searchByEmail(String keyword) {
        return userRepository.searchByEmail(keyword);
    }

    // Custom — korisnici sa aktivnim tokenima
    public List<User> getUsersWithActiveTokens() {
        return userRepository.findUsersWithActiveTokens();
    }

    // Batch unos
    @Transactional
    public List<User> createBatch(List<User> users) {
        return userRepository.saveAll(users);
    }

    // Transakcijska metoda — kreira korisnika i audit log u jednoj transakciji
    @Transactional
    public User createWithAuditLog(User user) {
        User saved = userRepository.save(user);
        AuditLog log = new AuditLog(saved, "USER_CREATED");
        auditLogRepository.save(log);
        return saved;
    }
}