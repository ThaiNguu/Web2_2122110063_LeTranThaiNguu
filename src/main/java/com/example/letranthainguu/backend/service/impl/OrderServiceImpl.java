package com.example.letranthainguu.backend.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.letranthainguu.backend.domain.Cart;
import com.example.letranthainguu.backend.domain.CartItem;
import com.example.letranthainguu.backend.domain.Order;
import com.example.letranthainguu.backend.domain.OrderItem;
import com.example.letranthainguu.backend.domain.Payment;
import com.example.letranthainguu.backend.domain.Product;
import com.example.letranthainguu.backend.exceptions.APIException;
import com.example.letranthainguu.backend.exceptions.ResourceNotFoundException;
import com.example.letranthainguu.backend.dto.OrderDTO;
import com.example.letranthainguu.backend.dto.OrderItemDTO;
import com.example.letranthainguu.backend.dto.OrderResponse;
import com.example.letranthainguu.backend.repository.CartItemRepo;
import com.example.letranthainguu.backend.repository.CartRepo;
import com.example.letranthainguu.backend.repository.OrderItemRepo;
import com.example.letranthainguu.backend.repository.OrderRepo;
import com.example.letranthainguu.backend.repository.PaymentRepo;
import com.example.letranthainguu.backend.repository.UserRepo;
import com.example.letranthainguu.backend.service.CartService;
import com.example.letranthainguu.backend.service.OrderService;
import com.example.letranthainguu.backend.service.UserService;

import jakarta.transaction.Transactional;

@Transactional
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired

    public UserRepo userRepo;
    @Autowired
    public CartRepo cartRepo;
    @Autowired
    public OrderRepo orderRepo;
    @Autowired
    private PaymentRepo paymentRepo;
    @Autowired
    public OrderItemRepo orderItemRepo;
    @Autowired
    public CartItemRepo cartItemRepo;
    @Autowired
    public UserService userService;
    @Autowired
    public CartService cartService;
    @Autowired
    public ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderDTO placeOrder(String email, Long cartId, String paymentMethod) {
        // Lấy thông tin giỏ hàng
        Cart cart = cartRepo.findCartByEmailAndCartId(email, cartId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart", "cartId", cartId);
        }

        // Tạo payment
        Payment payment = new Payment();
        payment.setPaymentMethod(paymentMethod);
        paymentRepo.save(payment);

        // Tạo order
        Order order = new Order();
        order.setEmail(email);
        order.setOrderDate(LocalDate.now());
        order.setOrderStatus("PENDING");
        order.setPayment(payment);
        order.setTotalAmount(cart.getTotalPrice());

        // Lưu order
        Order savedOrder = orderRepo.save(order);

        // Chuyển cart items thành order items
        List<CartItem> cartItems = new ArrayList<>(cart.getCartItems());
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());
            orderItemRepo.save(orderItem);

            // Xóa cart item
            cartItemRepo.deleteCartItemByProductIdAndCartId(cart.getCartId(), cartItem.getProduct().getProductId());
        }

        // Cập nhật lại tổng giá cart
        cart.setTotalPrice(0.0);
        cartRepo.save(cart);

        // Chuyển đổi sang DTO và trả về
        return modelMapper.map(savedOrder, OrderDTO.class);
    }

    @Override
    public List<OrderDTO> getOrdersByUser(String emailId) {
        List<Order> orders = orderRepo.findAllByEmail(emailId);
        List<OrderDTO> orderDTOS = orders.stream().map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());

        if (orderDTOS.size() == 0) {
            throw new APIException("No orders placed yet by the user with email:" + emailId);
        }
        return orderDTOS;
    }

    @Override
    public OrderDTO getOrder(String emailId, Long orderId) {
        Order order = orderRepo.findOrderByEmailAndOrderId(emailId, orderId);
        if (order == null) {
            throw new ResourceNotFoundException("Order", "orderId", orderId);
        }
        return modelMapper.map(order, OrderDTO.class);
    }

    @Override
    public OrderResponse getAllOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Order> pageOrders = orderRepo.findAll(pageDetails);
        List<Order> orders = pageOrders.getContent();
        List<OrderDTO> orderDTOS = orders.stream().map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());

        if (orderDTOS.size() == 0) {
            throw new APIException("No orders placed yet by the users");
        }

        OrderResponse orderResponse = new OrderResponse();

        orderResponse.setContent(orderDTOS);
        orderResponse.setPageNumber(pageOrders.getNumber());
        orderResponse.setPageSize(pageOrders.getSize());
        orderResponse.setTotalElements(pageOrders.getTotalElements());
        orderResponse.setTotalPages(pageOrders.getTotalPages());
        orderResponse.setLastPage(pageOrders.isLast());
        return orderResponse;
    }

    @Override
    public OrderDTO updateOrder(String emailId, Long orderId, String orderStatus) {
        Order order = orderRepo.findOrderByEmailAndOrderId(emailId, orderId);
        if (order == null) {
            throw new ResourceNotFoundException("Order", "orderId", orderId);
        }
        order.setOrderStatus(orderStatus);
        return modelMapper.map(order, OrderDTO.class);
    }
}