package com.example.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDTO {

    private Long userId;

    @Email(message = "Email nije validan.")
    @NotBlank(message = "Email ne smije biti prazan.")
    private String email;

    @NotBlank(message = "Lozinka ne smije biti prazna.")
    private String password;

    private Long roleId;
}