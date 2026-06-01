package com.example.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {

    @NotBlank(message = "Email ne smije biti prazan.")
    @Email(message = "Email mora biti ispravnog formata.")
    private String email;

    @NotBlank(message = "Lozinka ne smije biti prazna.")
    private String password;

}
