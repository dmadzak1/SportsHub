package com.sportshub.facility.dto;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Long userId;
    private String email;
    private Long roleId;
}