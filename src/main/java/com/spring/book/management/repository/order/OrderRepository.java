package com.spring.book.management.repository.order;

import com.spring.book.management.model.Order;
import com.spring.book.management.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUser(User user);
}
