package com.example.letranthainguu.example05.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.letranthainguu.example05.entity.Payment;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long> {

}
