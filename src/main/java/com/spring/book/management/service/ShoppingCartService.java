package com.spring.book.management.service;

import com.spring.book.management.dto.shoppingcart.AddToCartRequestDto;
import com.spring.book.management.dto.shoppingcart.ShoppingCartResponseDto;
import com.spring.book.management.dto.shoppingcart.UpdateCartItemRequestDto;

public interface ShoppingCartService {
    ShoppingCartResponseDto getCartForCurrentUser();

    ShoppingCartResponseDto addToCart(AddToCartRequestDto dto);

    ShoppingCartResponseDto updateCartItem(Long cartItemId,
                                           UpdateCartItemRequestDto dto);

    void removeCartItem(Long cartItemId);
}
