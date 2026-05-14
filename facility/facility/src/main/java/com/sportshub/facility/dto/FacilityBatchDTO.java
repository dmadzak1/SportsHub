package com.sportshub.facility.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class FacilityBatchDTO {

    @NotEmpty(message = "Lista terena ne smije biti prazna.")
    @Valid
    private List<FacilityDTO> facilities;
}