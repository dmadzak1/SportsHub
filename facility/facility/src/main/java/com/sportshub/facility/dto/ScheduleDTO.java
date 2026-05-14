package com.sportshub.facility.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ScheduleDTO {

    private Long scheduleId;

    @NotNull(message = "ID terena ne smije biti null.")
    private Long facilityId;

    @NotNull(message = "Datum ne smije biti null.")
    private LocalDate date;

    @NotNull(message = "Termin ne smije biti null.")
    private LocalTime timeSlot;
}