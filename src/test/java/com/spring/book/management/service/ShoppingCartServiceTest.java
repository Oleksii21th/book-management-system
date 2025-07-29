package com.spring.book.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.spring.book.management.dto.shoppingcart.AddToCartRequestDto;
import com.spring.book.management.dto.shoppingcart.CartItemResponseDto;
import com.spring.book.management.dto.shoppingcart.ShoppingCartResponseDto;
import com.spring.book.management.dto.shoppingcart.UpdateCartItemRequestDto;
import com.spring.book.management.exception.BookNotFoundException;
import com.spring.book.management.exception.CartItemNotFoundException;
import com.spring.book.management.mapper.ShoppingCartMapper;
import com.spring.book.management.model.Book;
import com.spring.book.management.model.CartItem;
import com.spring.book.management.model.ShoppingCart;
import com.spring.book.management.model.User;
import com.spring.book.management.repository.book.BookRepository;
import com.spring.book.management.repository.cartitem.CartItemRepository;
import com.spring.book.management.repository.shoppingcart.ShoppingCartRepository;
import com.spring.book.management.service.impl.ShoppingCartServiceImpl;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTest {
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private UserService userService;
    @Mock
    private ShoppingCartMapper cartMapper;

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    private Book book;
    private User user;
    private CartItem cartItem;
    private CartItemResponseDto cartItemResponseDto;
    private ShoppingCart shoppingCart;
    private ShoppingCartResponseDto shoppingCartResponseDto;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Test");

        user = new User();
        user.setId(1L);

        shoppingCart = new ShoppingCart(user);

        cartItem = new CartItem(shoppingCart, book, 2);

       cartItemResponseDto = new CartItemResponseDto(
                1L,
                1L,
                "Test",
                2);

        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(cartItem);

        List<CartItemResponseDto> cartItemDtos = new ArrayList<>();
        cartItemDtos.add(cartItemResponseDto);

        shoppingCart.setCartItems(cartItems);

        shoppingCartResponseDto = new ShoppingCartResponseDto(
                1L,
                user.getId(),
                cartItemDtos);
    }

    @Test
    @DisplayName("Returns current user's cart – existing cart")
    void getCartForCurrentUser_ExistingCart_ReturnsDto() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(shoppingCartRepository.findByUser(user))
                .thenReturn(Optional.of(shoppingCart));
        when(cartMapper.toDto(shoppingCart))
                .thenReturn(shoppingCartResponseDto);

        ShoppingCartResponseDto result =
                shoppingCartService.getCartForCurrentUser();

        assertThat(result).isEqualTo(shoppingCartResponseDto);
        verify(shoppingCartRepository).findByUser(user);
        verify(shoppingCartRepository, never()).save(any());
    }

    @Test
    @DisplayName("Creates a new cart if the user doesn't have one")
    void getCartForCurrentUser_NoCart_CreatesAndReturnsDto() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(shoppingCartRepository.findByUser(user))
                .thenReturn(Optional.empty());
        when(shoppingCartRepository.save(any(ShoppingCart.class)))
                .thenReturn(shoppingCart);
        when(cartMapper.toDto(shoppingCart))
                .thenReturn(shoppingCartResponseDto);

        ShoppingCartResponseDto result =
                shoppingCartService.getCartForCurrentUser();

        assertThat(result).isEqualTo(shoppingCartResponseDto);
        verify(shoppingCartRepository).save(any(ShoppingCart.class));
    }

    @Test
    @DisplayName("Adds a new item to an empty cart")
    void addToCart_NewItem_AddsItem() {
        AddToCartRequestDto dto = new AddToCartRequestDto(book.getId(), 1);
        shoppingCart.setCartItems(new HashSet<>());

        when(userService.getCurrentUser()).thenReturn(user);
        when(shoppingCartRepository.findByUser(user))
                .thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(book.getId()))
                .thenReturn(Optional.of(book));
        when(shoppingCartRepository.save(shoppingCart))
                .thenReturn(shoppingCart);
        when(cartMapper.toDto(shoppingCart))
                .thenReturn(shoppingCartResponseDto);

        ShoppingCartResponseDto result =
                shoppingCartService.addToCart(dto);

        assertThat(result).isEqualTo(shoppingCartResponseDto);
        verify(shoppingCartRepository).save(shoppingCart);
        assertThat(shoppingCart.getCartItems()).hasSize(1);
    }

    @Test
    @DisplayName("Increases quantity if item already exists in the cart")
    void addToCart_ExistingItem_IncrementsQuantity() {
        AddToCartRequestDto dto = new AddToCartRequestDto(book.getId(), 3);

        when(userService.getCurrentUser()).thenReturn(user);
        when(shoppingCartRepository.findByUser(user))
                .thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(book.getId()))
                .thenReturn(Optional.of(book));
        when(shoppingCartRepository.save(shoppingCart))
                .thenReturn(shoppingCart);
        when(cartMapper.toDto(shoppingCart))
                .thenReturn(shoppingCartResponseDto);

        shoppingCartService.addToCart(dto);

        assertThat(cartItem.getQuantity()).isEqualTo(5);
        verify(shoppingCartRepository).save(shoppingCart);
    }

    @Test
    @DisplayName("Throws BookNotFoundException when the book is not found")
    void addToCart_BookNotFound_ThrowsException() {
        AddToCartRequestDto dto = new AddToCartRequestDto(999L, 1);

        when(userService.getCurrentUser()).thenReturn(user);
        when(shoppingCartRepository.findByUser(user)).thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class,
                () -> shoppingCartService.addToCart(dto));
        verify(shoppingCartRepository, never()).save(any());
    }

    @Test
    @DisplayName("Updates the quantity of an existing cart item")
    void updateCartItem_ExistingItem_UpdatesQuantity() {
        UpdateCartItemRequestDto dto =
                new UpdateCartItemRequestDto(7);

        when(cartItemRepository.findById(
                cartItem.getId())).thenReturn(Optional.of(cartItem));
        when(cartMapper.toDto(shoppingCart))
                .thenReturn(shoppingCartResponseDto);

        ShoppingCartResponseDto result =
                shoppingCartService.updateCartItem(cartItem.getId(), dto);

        assertThat(result).isEqualTo(shoppingCartResponseDto);
        assertThat(cartItem.getQuantity()).isEqualTo(7);
        verify(cartItemRepository).save(cartItem);
    }

    @Test
    @DisplayName("Throws CartItemNotFoundException when the item is not found")
    void updateCartItem_NotFound_ThrowsException() {
        when(cartItemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CartItemNotFoundException.class,
                () -> shoppingCartService.updateCartItem(
                        999L,
                        new UpdateCartItemRequestDto(1)));
        verify(cartItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Removes an item from the cart")
    void removeCartItem_ValidId_DeletesItem() {
        when(cartItemRepository.findById(cartItem.getId())).thenReturn(Optional.of(cartItem));
        doNothing().when(cartItemRepository).delete(cartItem);

        shoppingCartService.removeCartItem(cartItem.getId());

        verify(cartItemRepository).delete(cartItem);
    }

    @Test
    @DisplayName("Throws CartItemNotFoundException when the item is not found")
    void removeCartItem_NotFound_ThrowsException() {
        when(cartItemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CartItemNotFoundException.class,
                () -> shoppingCartService.removeCartItem(999L));
        verify(cartItemRepository, never()).delete(any());
    }
}