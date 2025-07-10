package com.spring.book.management.service;

import com.spring.book.management.dto.order.OrderRequestDto;
import com.spring.book.management.dto.order.OrderResponseDto;
import com.spring.book.management.model.Status;
import java.util.List;

public interface OrderService {
    OrderResponseDto save(OrderRequestDto dto);

    List<OrderResponseDto> findAll();

    OrderResponseDto updateOrderStatus(Long orderId, Status newStatus);

    OrderResponseDto getOrderById(Long orderId);
}
