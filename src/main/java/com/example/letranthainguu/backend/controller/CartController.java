package com.example.letranthainguu.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.letranthainguu.backend.dto.CartDTO;
import com.example.letranthainguu.backend.service.CartService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
public class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping("/public/carts/{cartId}/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long cartId, @PathVariable Long productId,
            @PathVariable Integer quantity) {
        CartDTO cartDTO = cartService.addProductToCart(cartId, productId, quantity);
        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.CREATED);
    }

    @GetMapping("/admin/carts")
    public ResponseEntity<List<CartDTO>> getCarts() {
        List<CartDTO> cartDTOs = cartService.getAllCarts();
        return new ResponseEntity<List<CartDTO>>(cartDTOs, HttpStatus.OK);
    }

    @GetMapping("/public/user/{emailId}/carts/{cartId}")
    public ResponseEntity<CartDTO> getCartById(@PathVariable String emailId, @PathVariable Long cartId) {
        CartDTO cartDTO = cartService.getCart(emailId, cartId);
        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.OK);
    }

    @PutMapping("/public/carts/{cartId}/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> updateCartProduct(@PathVariable Long cartId, @PathVariable Long productId,
            @PathVariable Integer quantity) {
        CartDTO cartDTO = cartService.updateProductQuantityInCart(cartId, productId, quantity);
        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.OK);
    }

    @DeleteMapping("/public/carts/{cartId}/product/{productId}")
    public ResponseEntity<?> deleteProductFromCart(@PathVariable Long cartId, @PathVariable Long productId) {
        cartService.deleteProductFromCart(cartId, productId);
        return new ResponseEntity<>(Map.of("message", "Đã xóa sản phẩm khỏi giỏ hàng"), HttpStatus.OK);
    }
}