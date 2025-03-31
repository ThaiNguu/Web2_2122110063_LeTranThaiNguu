package com.example.letranthainguu.backend.service;

import java.util.List;

import com.example.letranthainguu.backend.dto.CartDTO;

public interface CartService {
    CartDTO addProductToCart(Long cartId, Long productId, Integer quantity);

    List<CartDTO> getAllCarts();

    CartDTO getCart(String emailId, Long cartId);

    CartDTO updateProductQuantityInCart(Long cartId, Long productId, Integer quantity);

    void updateProductInCarts(Long cartId, Long productId);

    void deleteProductFromCart(Long cartId, Long productId);
}
