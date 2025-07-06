package com.spring.book.management.mapper;

import com.spring.book.management.config.MapperConfig;
import com.spring.book.management.dto.CartItemResponseDto;
import com.spring.book.management.dto.ShoppingCartResponseDto;
import com.spring.book.management.model.CartItem;
import com.spring.book.management.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface ShoppingCartMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "cartItems", target = "cartItems")
    ShoppingCartResponseDto toDto(ShoppingCart cart);

    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    CartItemResponseDto toDto(CartItem item);
}
