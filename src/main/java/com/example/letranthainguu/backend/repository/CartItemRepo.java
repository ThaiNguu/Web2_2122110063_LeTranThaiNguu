package com.example.letranthainguu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.example.letranthainguu.backend.domain.CartItem;
import com.example.letranthainguu.backend.domain.Product;

public interface CartItemRepo extends JpaRepository<CartItem, Long> {

    @Query("SELECT ci.product FROM CartItem ci WHERE ci.product.id = ?1")
    Product findProductById(Long productId);

    // @Query("SELECT ci.cart FROM CartItem ci WHERE ci.product.id = ?1")
    // List<Cart> findCartsByProductId (Long productId);

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = ?1 AND ci.product.id = ?2")
    CartItem findCartItemByProductIdAndCartId(Long cartId, Long productId);

    // @Query("SELECT ci.cart FROM CartItem ci WHERE ci.cart.user.email = ?1 AND
    // ci.cart.id = ?2")
    // Cart findCartByEmailAndCartId(String email, Integer cartId);
    // @Query("SELECT ci.order FROM CartItem ci WHERE ci.order.user.email ?1 AND
    // ci.order.id = ?2")
    // Order findOrderByEmailAndOrderId(String email, Integer orderId);

    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = ?1 AND ci.product.id= ?2")
    void deleteCartItemByProductIdAndCartId(Long cartId, Long productId);
}
