package com.spring.book.management.repository.cartitem;

import com.spring.book.management.model.CartItem;
import com.spring.book.management.model.ShoppingCart;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByShoppingCart(ShoppingCart cart);
}
