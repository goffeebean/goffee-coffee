package com.goffeebean.exception;

public class RoastNotFoundException extends RuntimeException {

    public RoastNotFoundException(Long id) {
        super("Roast with id %d was not found".formatted(id));
    }
}
