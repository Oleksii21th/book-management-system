package com.spring.book.management.service.impl;

import com.spring.book.management.dto.shoppingcart.AddToCartRequestDto;
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
import com.spring.book.management.service.ShoppingCartService;
import com.spring.book.management.service.UserService;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserService userService;
    private final ShoppingCartMapper cartMapper;

    public ShoppingCartServiceImpl(ShoppingCartRepository shoppingCartRepository,
                                   CartItemRepository cartItemRepository,
                                   BookRepository bookRepository,
                                   UserService userService,
                                   ShoppingCartMapper cartMapper) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
        this.userService = userService;
        this.cartMapper = cartMapper;
    }

    @Override
    public ShoppingCartResponseDto getCartForCurrentUser() {
        User user = getCurrentUser();
        ShoppingCart cart = getOrCreateCartForUser(user);
        return cartMapper.toDto(cart);
    }

    @Override
    public ShoppingCartResponseDto addToCart(AddToCartRequestDto dto) {
        User user = getCurrentUser();
        ShoppingCart cart = getOrCreateCartForUser(user);

        Book book = bookRepository.findById(dto.bookId())
                .orElseThrow(() -> new BookNotFoundException("Book not found"));

        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getBook().getId().equals(dto.bookId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + dto.quantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setBook(book);
            newItem.setQuantity(dto.quantity());
            newItem.setShoppingCart(cart);
            cart.getCartItems().add(newItem);
        }

        shoppingCartRepository.save(cart);
        return cartMapper.toDto(cart);
    }

    @Override
    public ShoppingCartResponseDto updateCartItem(Long cartItemId, UpdateCartItemRequestDto dto) {
        CartItem item = getCartItemById(cartItemId);
        item.setQuantity(dto.quantity());
        cartItemRepository.save(item);
        return cartMapper.toDto(item.getShoppingCart());
    }

    @Override
    public void removeCartItem(Long cartItemId) {
        CartItem item = getCartItemById(cartItemId);
        cartItemRepository.delete(item);
    }

    private User getCurrentUser() {
        return userService.getCurrentUser();
    }

    private CartItem getCartItemById(Long cartItemId) {
        return cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException(cartItemId));
    }

    private ShoppingCart getOrCreateCartForUser(User user) {
        return shoppingCartRepository.findByUser(user)
                .orElseGet(() -> shoppingCartRepository.save(new ShoppingCart(user)));
    }
}
