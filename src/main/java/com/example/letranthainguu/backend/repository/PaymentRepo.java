package com.example.letranthainguu.backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.letranthainguu.backend.domain.Payment;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long> {

}
