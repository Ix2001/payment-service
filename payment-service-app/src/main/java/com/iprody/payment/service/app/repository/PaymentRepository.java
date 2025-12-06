package com.iprody.payment.service.app.repository;

import com.iprody.payment.service.app.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}
