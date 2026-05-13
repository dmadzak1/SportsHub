package com.sportshub.facility.client;

import com.sportshub.facility.dto.UserResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserServiceClientFallback implements UserServiceClient {

    private static final Logger log = LoggerFactory.getLogger(UserServiceClientFallback.class);

    @Override
    public UserResponseDTO getUserById(Long id) {
        log.error("User servis nije dostupan, fallback za userId: {}", id);
        return null; // null signalizira kontroleru da servis nije dostupan
    }
}