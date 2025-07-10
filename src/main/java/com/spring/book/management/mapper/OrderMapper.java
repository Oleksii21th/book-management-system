package com.spring.book.management.mapper;

import com.spring.book.management.config.MapperConfig;
import com.spring.book.management.dto.order.OrderItemDto;
import com.spring.book.management.dto.order.OrderResponseDto;
import com.spring.book.management.model.Order;
import com.spring.book.management.model.OrderItem;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface OrderMapper {
    @Mapping(source = "user.id", target = "userId")
    OrderResponseDto toDto(Order order);

    @Mapping(source = "book.id", target = "bookId")
    OrderItemDto toDto(OrderItem orderItem);

    List<OrderResponseDto> toDtoList(List<Order> orders);
}
