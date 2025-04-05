package com.spring.book.management.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(Long id) {
        super("Book with ID " + id + " not found");
    }
}
