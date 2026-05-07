package com.example.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleDTO {

    private Long roleId;

    @NotBlank(message = "Naziv uloge ne smije biti prazan.")
    private String roleName;
}