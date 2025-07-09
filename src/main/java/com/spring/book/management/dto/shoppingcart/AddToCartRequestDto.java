package com.spring.book.management.dto.shoppingcart;

public record AddToCartRequestDto(Long bookId, int quantity) {
}
