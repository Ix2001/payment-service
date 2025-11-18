package com.iprody.payment.service.app.controllers;

import com.iprody.payment.service.app.models.Payment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private static final Map<Long, Payment> payments = new HashMap<>();

    static {
        payments.put(1L, new Payment(1L, 1.0));
        payments.put(2L, new Payment(2L, 2.0));
        payments.put(3L, new Payment(3L, 3.0));
        payments.put(4L, new Payment(4L, 4.0));
        payments.put(5L, new Payment(5L, 5.0));
    }


    @GetMapping("/{id}")
    public Payment getPaymentById(@PathVariable Long id) {
        return payments.get(id);
    }

    @GetMapping()
    public List<Payment> getAllPayments() {
        return new ArrayList<>(payments.values());
    }


}
