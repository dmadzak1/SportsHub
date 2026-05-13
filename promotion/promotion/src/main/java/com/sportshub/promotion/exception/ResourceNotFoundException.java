package com.sportshub.promotion.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " sa ID-om " + id + " nije pronađen.");
    }
}
