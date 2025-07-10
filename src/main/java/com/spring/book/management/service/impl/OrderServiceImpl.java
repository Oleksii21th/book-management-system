package com.spring.book.management.service.impl;

import com.spring.book.management.dto.order.OrderRequestDto;
import com.spring.book.management.dto.order.OrderResponseDto;
import com.spring.book.management.exception.OrderItemNotFoundException;
import com.spring.book.management.exception.ShoppingCartNotFoundException;
import com.spring.book.management.mapper.OrderMapper;
import com.spring.book.management.model.Book;
import com.spring.book.management.model.CartItem;
import com.spring.book.management.model.Order;
import com.spring.book.management.model.OrderItem;
import com.spring.book.management.model.ShoppingCart;
import com.spring.book.management.model.Status;
import com.spring.book.management.model.User;
import com.spring.book.management.repository.order.OrderRepository;
import com.spring.book.management.repository.shoppingcart.ShoppingCartRepository;
import com.spring.book.management.service.OrderService;
import com.spring.book.management.service.UserService;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final UserService userService;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository orderRepository,
                            ShoppingCartRepository shoppingCartRepository,
                            UserService userService,
                            OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.shoppingCartRepository = shoppingCartRepository;
        this.userService = userService;
        this.orderMapper = orderMapper;
    }

    @Override
    public OrderResponseDto save(OrderRequestDto dto) {
        User user = userService.getCurrentUser();
        ShoppingCart shoppingCart = shoppingCartRepository.findByUser(user)
                .orElseThrow(ShoppingCartNotFoundException::new);

        if (shoppingCart.getCartItems() == null
                || shoppingCart.getCartItems().isEmpty()) {
            throw new ShoppingCartNotFoundException("Shopping cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(Status.PENDING);
        order.setShippingAddress(dto.shippingAddress());
        order.setOrderDate(LocalDateTime.now());
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : shoppingCart.getCartItems()) {
            Book book = cartItem.getBook();
            int quantity = cartItem.getQuantity();
            BigDecimal price = book.getPrice();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(book);
            orderItem.setQuantity(quantity);
            orderItem.setPrice(price.multiply(BigDecimal.valueOf(quantity)));

            order.getOrderItems().add(orderItem);
            total = total.add(orderItem.getPrice());
        }

        order.setTotal(total);
        orderRepository.save(order);

        shoppingCart.getCartItems().clear();
        shoppingCartRepository.save(shoppingCart);

        return orderMapper.toDto(order);
    }

    @Override
    public List<OrderResponseDto> findAll() {
        User user = userService.getCurrentUser();
        List<Order> orders = orderRepository.findAllByUser(user);
        return orderMapper.toDtoList(orders);
    }

    @Override
    public OrderResponseDto updateOrderStatus(Long orderId, Status newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderItemNotFoundException(orderId));
        order.setStatus(newStatus);
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    @Override
    public OrderResponseDto getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderItemNotFoundException(orderId));
        return orderMapper.toDto(order);
    }
}
