package com.example.user.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class UserBatchDTO {

    @NotEmpty(message = "Lista korisnika ne smije biti prazna.")
    @Valid
    private List<UserDTO> users;
}