package com.spring.book.management.exception;

public class ShoppingCartNotFoundException extends RuntimeException {
    public ShoppingCartNotFoundException() {
        super("Shopping cart not found");
    }

    public ShoppingCartNotFoundException(String message) {
        super(message);
    }
}
