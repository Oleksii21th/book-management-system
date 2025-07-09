package com.spring.book.management.dto.shoppingcart;

import java.util.List;

public record ShoppingCartResponseDto(
        Long id,
        Long userId,
        List<CartItemResponseDto> cartItems) {
}
