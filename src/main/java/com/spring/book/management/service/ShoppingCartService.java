package com.spring.book.management.service;

import com.spring.book.management.dto.AddToCartRequestDto;
import com.spring.book.management.dto.ShoppingCartResponseDto;
import com.spring.book.management.dto.UpdateCartItemRequestDto;

public interface ShoppingCartService {
    ShoppingCartResponseDto getCartForCurrentUser();

    ShoppingCartResponseDto addToCart(AddToCartRequestDto dto);

    ShoppingCartResponseDto updateCartItem(Long cartItemId,
                                           UpdateCartItemRequestDto dto);

    void removeCartItem(Long cartItemId);
}
