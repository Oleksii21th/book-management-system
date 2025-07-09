package com.spring.book.management.exception;

public class OrderItemNotFoundException extends RuntimeException {
    public OrderItemNotFoundException(Long orderId) {
        super("Order not found with id: " + orderId);
    }
}
