package com.spring.book.management.exception;

public class LoginException extends RuntimeException {
    public LoginException(String message) {
        super(message);
    }
}