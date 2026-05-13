package com.sportshub.facility.client;

import com.sportshub.facility.dto.UserResponseDTO;
import com.sportshub.facility.exception.UserServiceException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user", fallback = UserServiceClientFallback.class)
public interface UserServiceClient {

    @GetMapping("/users/{id}")
    UserResponseDTO getUserById(@PathVariable("id") Long id);
}