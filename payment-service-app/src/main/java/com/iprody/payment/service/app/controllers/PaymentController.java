package com.iprody.payment.service.app.controllers;

import com.iprody.payment.service.app.models.Payment;
import com.iprody.payment.service.app.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/{id}")
    public Payment getPaymentById(@PathVariable UUID id) {
        return paymentService.getPaymentById(id);
    }

    @GetMapping()
    public List<Payment> getAllPayments() {
        return paymentService.getPayments();
    }
}
