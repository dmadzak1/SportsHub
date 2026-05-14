package com.sportshub.facility.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class ReservationBatchDTO {

    @NotEmpty(message = "Lista rezervacija ne smije biti prazna.")
    @Valid
    private List<ReservationDTO> reservations;
}