package com.spring.book.management.controller;

import com.spring.book.management.dto.shoppingcart.AddToCartRequestDto;
import com.spring.book.management.dto.shoppingcart.ShoppingCartResponseDto;
import com.spring.book.management.dto.shoppingcart.UpdateCartItemRequestDto;
import com.spring.book.management.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping Cart", description = "Endpoints for managing shopping cart")
@RestController
@RequestMapping("/api/cart")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ShoppingCartResponseDto getCart() {
        return shoppingCartService.getCartForCurrentUser();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ShoppingCartResponseDto addToCart(@Valid @RequestBody AddToCartRequestDto dto) {
        return shoppingCartService.addToCart(dto);
    }

    @PutMapping("/cart-items/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ShoppingCartResponseDto updateCartItem(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCartItemRequestDto dto) {
        return shoppingCartService.updateCartItem(id, dto);
    }

    @DeleteMapping("/cart-items/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public void deleteCartItem(@PathVariable Long id) {
        shoppingCartService.removeCartItem(id);
    }
}
