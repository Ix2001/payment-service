package com.iprody.payment.service.app.service;

import com.iprody.payment.service.app.models.Payment;
import com.iprody.payment.service.app.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public Payment getPaymentById(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));
    }

    public List<Payment> getPayments() {
        return paymentRepository.findAll();
    }
}
