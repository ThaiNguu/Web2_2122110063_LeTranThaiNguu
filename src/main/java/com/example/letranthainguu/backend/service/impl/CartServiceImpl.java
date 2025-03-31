package com.example.letranthainguu.backend.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.letranthainguu.backend.domain.Cart;
import com.example.letranthainguu.backend.domain.CartItem;
import com.example.letranthainguu.backend.domain.Product;
import com.example.letranthainguu.backend.exceptions.APIException;
import com.example.letranthainguu.backend.exceptions.ResourceNotFoundException;
import com.example.letranthainguu.backend.dto.CartDTO;
import com.example.letranthainguu.backend.dto.ProductDTO;
import com.example.letranthainguu.backend.repository.CartItemRepo;
import com.example.letranthainguu.backend.repository.CartRepo;
import com.example.letranthainguu.backend.repository.ProductRepo;
import com.example.letranthainguu.backend.service.CartService;

import jakarta.transaction.Transactional;

@Transactional
@Service

public class CartServiceImpl implements CartService {
    @Autowired
    private CartRepo cartRepo;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private CartItemRepo cartItemRepo;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CartDTO addProductToCart(Long cartId, Long productId, Integer quantity) {
        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("product", "productId", productId));
        CartItem cartItem = cartItemRepo.findCartItemByProductIdAndCartId(cartId, productId);
        if (cartItem != null) {
            throw new APIException("Product" + product.getProductName() + " already exists in the cart");

        }
        if (product.getQuantity() == 0) {
            throw new APIException(product.getProductName() + " is not available");

        }
        if (product.getQuantity() < quantity) {
            throw new APIException("Please, make an order of the " + product.getProductName()
                    + " less than or equal to the quantity" + product.getQuantity() + ".");

        }
        CartItem newCartItem = new CartItem();
        newCartItem.setCart(cart);
        newCartItem.setProduct(product);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());
        cartItemRepo.save(newCartItem);
        product.setQuantity(product.getQuantity() - quantity);
        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<ProductDTO> productDTOs = cart.getCartItems().stream()
                .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class)).collect(Collectors.toList());

        cartDTO.setProducts(productDTOs);
        return cartDTO;

    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepo.findAll();

        if (carts.size() == 0) {
            throw new APIException("No carts exists");
        }

        List<CartDTO> cartDTOs = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
            List<ProductDTO> products = cart.getCartItems().stream()
                    .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class)).collect(Collectors.toList());
            cartDTO.setProducts(products);
            return cartDTO;
        }).collect(Collectors.toList());
        return cartDTOs;

    }

    @Override
    public CartDTO getCart(String emailId, Long cartId) {
        Cart cart = cartRepo.findCartByEmailAndCartId(emailId, cartId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart", "cartId", cartId);
        }
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        
        // Lấy danh sách sản phẩm và số lượng từ cart_items
        List<ProductDTO> products = cart.getCartItems().stream()
                .map(cartItem -> {
                    ProductDTO productDTO = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
                    productDTO.setCartQuantity(cartItem.getQuantity()); // Set số lượng từ cart_items
                    return productDTO;
                })
                .collect(Collectors.toList());
        
        cartDTO.setProducts(products);
        return cartDTO;
    }

    @Override
    public void updateProductInCarts(Long cartId, Long productId) {
        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("product", "productId", productId));
        CartItem cartItem = cartItemRepo.findCartItemByProductIdAndCartId(cartId, productId);
        if (cartItem != null) {
            throw new APIException("Product " + product.getProductName() + " not availble in the cart");
        }
        Double cartPrice = cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());
        cartItem.setProductPrice(product.getSpecialPrice());
        cart.setTotalPrice(cartPrice + (cartItem.getProductPrice() * cartItem.getQuantity()));
        cartItem = cartItemRepo.save(cartItem);
    }

    @Override
    public CartDTO updateProductQuantityInCart(Long cartId, Long productId, Integer quantity) {
        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("product", "productId", productId));

        if (product.getQuantity() == 0) {
            throw new APIException(product.getProductName() + " is not available");

        }
        if (product.getQuantity() < quantity) {
            throw new APIException("Please, make an order of the " + product.getProductName()
                    + " less than or equal to the quantity" + product.getQuantity() + ".");

        }
        CartItem cartItem = cartItemRepo.findCartItemByProductIdAndCartId(cartId, productId);
        if (cartItem == null) {
            throw new APIException("Product " + product.getProductName() + " not available in the cart!!");
        }
        Double cartPrice = cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());
        product.setQuantity(product.getQuantity() + cartItem.getQuantity() - quantity);
        cartItem.setProductPrice(product.getSpecialPrice());
        cartItem.setQuantity(quantity);
        cartItem.setDiscount(product.getDiscount());
        cart.setTotalPrice(cartPrice + (cartItem.getProductPrice() * quantity));
        cartItem = cartItemRepo.save(cartItem);
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<ProductDTO> productDTOs = cart.getCartItems().stream()
                .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class)).collect(Collectors.toList());
        cartDTO.setProducts(productDTOs);
        return cartDTO;

    }

    @Override
    @Transactional
    public void deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        CartItem cartItem = cartItemRepo.findCartItemByProductIdAndCartId(cartId, productId);
        if (cartItem == null) {
            throw new ResourceNotFoundException("CartItem", "productId", productId);
        }

        // Cập nhật tổng giá
        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity()));
        
        // Xóa cart item
        cartItemRepo.deleteCartItemByProductIdAndCartId(cartId, productId);
        cart.getCartItems().remove(cartItem);
        
        // Lưu lại cart
        cartRepo.save(cart);
    }
}