package com.sportshub.facility.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FacilityDTO {

    private Long facilityId;

    @NotBlank(message = "Naziv ne smije biti prazan.")
    private String name;

    @NotBlank(message = "Tip ne smije biti prazan.")
    private String type;
}