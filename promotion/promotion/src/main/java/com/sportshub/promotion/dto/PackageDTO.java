package com.sportshub.promotion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PackageDTO {

    private Long packageId;

    @NotBlank(message = "Naziv paketa ne smije biti prazan.")
    private String name;

    @NotNull(message = "Cijena ne smije biti null.")
    private Double price;
}
