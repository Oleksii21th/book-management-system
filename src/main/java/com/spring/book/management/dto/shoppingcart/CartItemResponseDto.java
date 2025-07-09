package com.spring.book.management.dto.shoppingcart;

public record CartItemResponseDto(
        Long id,
        Long bookId,
        String bookTitle,
        int quantity) {
}
