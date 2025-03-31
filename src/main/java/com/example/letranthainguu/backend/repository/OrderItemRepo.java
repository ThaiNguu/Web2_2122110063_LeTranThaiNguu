package com.example.letranthainguu.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.letranthainguu.backend.domain.OrderItem;

@Repository

public interface OrderItemRepo   extends JpaRepository<OrderItem,Long> {
    
}
