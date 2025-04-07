package com.spring.book.management.exception;

public class SpecificationNotFoundException extends RuntimeException {
    public SpecificationNotFoundException(String key) {
        super("Can't find correct specification for provider key: " + key);
    }
}
