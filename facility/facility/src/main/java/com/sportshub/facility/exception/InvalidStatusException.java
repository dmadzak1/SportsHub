package com.sportshub.facility.exception;

public class InvalidStatusException extends RuntimeException {

    public InvalidStatusException(String status) {
        super("Nevažeći status: '" + status + "'. Dozvoljeni statusi: PENDING, CONFIRMED, CANCELLED, EXPIRED.");
    }
}