package com.spring.book.management.dto;

import java.util.List;

public record ShoppingCartResponseDto(
        Long id,
        Long userId,
        List<CartItemResponseDto> cartItems) {
}
