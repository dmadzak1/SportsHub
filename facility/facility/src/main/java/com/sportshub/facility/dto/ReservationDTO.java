package com.sportshub.facility.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservationDTO {

    private Long reservationId;

    @NotNull(message = "ID korisnika ne smije biti null.")
    private Long userId;

    @NotNull(message = "ID termina ne smije biti null.")
    private Long scheduleId;

    private String status;
}