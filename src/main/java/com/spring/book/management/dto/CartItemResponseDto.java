package com.spring.book.management.dto;

public record CartItemResponseDto(
        Long id,
        Long bookId,
        String bookTitle,
        int quantity) {
}
