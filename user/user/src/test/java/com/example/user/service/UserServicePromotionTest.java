package com.example.user.service;

import com.example.user.client.PromotionServiceClient;
import com.example.user.dto.PromotionResponseDTO;
import com.example.user.dto.UserWithPromotionsDTO;
import com.example.user.model.Role;
import com.example.user.model.User;
import com.example.user.repository.AuditLogRepository;
import com.example.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServicePromotionTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private PromotionServiceClient promotionServiceClient;

    @InjectMocks
    private UserService userService;

    private User user;
    private PromotionResponseDTO promotion;

    @BeforeEach
    void setUp() {
        Role role = new Role("USER");
        role.setRoleId(1L);

        user = new User("ana@email.com", "pass", role);
        user.setUserId(1L);

        promotion = new PromotionResponseDTO();
        promotion.setPromotionId(1L);
        promotion.setDiscount(10.0);
        promotion.setValidUntil(LocalDate.now().plusMonths(1));
        promotion.setPackageId(1L);
    }

    @Test
    void getUserWithPromotions_returnsUserWithActivePromotions() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(promotionServiceClient.getActivePromotions()).thenReturn(List.of(promotion));

        UserWithPromotionsDTO result = userService.getUserWithPromotions(1L);

        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("ana@email.com");
        assertThat(result.getPromotions()).hasSize(1);
        assertThat(result.getPromotions().get(0).getDiscount()).isEqualTo(10.0);
    }

    @Test
    void getUserWithPromotions_promotionServiceUnavailable_returnsEmptyList() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(promotionServiceClient.getActivePromotions()).thenReturn(Collections.emptyList());

        UserWithPromotionsDTO result = userService.getUserWithPromotions(1L);

        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getPromotions()).isEmpty();
    }

    @Test
    void getUserWithPromotions_userNotFound_throwsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserWithPromotions(99L))
                .isInstanceOf(com.example.user.exception.ResourceNotFoundException.class);
    }
}