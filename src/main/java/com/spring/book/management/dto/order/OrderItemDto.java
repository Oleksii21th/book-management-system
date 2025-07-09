package com.spring.book.management.dto.order;

public record OrderItemDto(
        Long id,
        Long bookId,
        int quantity) {
}
