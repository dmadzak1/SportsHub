package com.example.user.service;

import com.example.user.client.PromotionServiceClient;
import com.example.user.dto.PromotionResponseDTO;
import com.example.user.dto.UserWithPromotionsDTO;
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
    private final PromotionServiceClient promotionServiceClient;

    public UserService(UserRepository userRepository,
                       AuditLogRepository auditLogRepository,
                       PromotionServiceClient promotionServiceClient) {
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
        this.promotionServiceClient = promotionServiceClient;
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

    public Page<User> getPaginated(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return userRepository.findAll(pageable);
    }

    public List<User> getByRole(String roleName) {
        return userRepository.findByRoleRoleName(roleName);
    }

    public List<User> searchByEmail(String keyword) {
        return userRepository.searchByEmail(keyword);
    }

    public List<User> getUsersWithActiveTokens() {
        return userRepository.findUsersWithActiveTokens();
    }

    @Transactional
    public List<User> createBatch(List<User> users) {
        return userRepository.saveAll(users);
    }

    @Transactional
    public User createWithAuditLog(User user) {
        User saved = userRepository.save(user);
        AuditLog log = new AuditLog(saved, "USER_CREATED");
        auditLogRepository.save(log);
        return saved;
    }

    public UserWithPromotionsDTO getUserWithPromotions(Long id) {
        User user = getById(id);
        List<PromotionResponseDTO> promotions = promotionServiceClient.getActivePromotions();

        UserWithPromotionsDTO dto = new UserWithPromotionsDTO();
        dto.setUserId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setRoleId(user.getRole() != null ? user.getRole().getRoleId() : null);
        dto.setPromotions(promotions);

        return dto;
    }
}