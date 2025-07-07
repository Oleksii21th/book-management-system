package com.spring.book.management.repository.shoppingcart;

import com.spring.book.management.model.ShoppingCart;
import com.spring.book.management.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    Optional<ShoppingCart> findByUser(User user);
}
