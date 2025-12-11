package com.iprody.payment.service.app.controllers;

import com.iprody.payment.service.app.models.Payment;
import com.iprody.payment.service.app.persistence.PaymentFilter;
import com.iprody.payment.service.app.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/{guid}")
    public Payment getPaymentByGuid(@PathVariable UUID guid) {
        return paymentService.getPaymentByGuid(guid);
    }

    @GetMapping()
    public List<Payment> getAllPayments() {
        return paymentService.getPayments();
    }

    @GetMapping("/search")
    public Page<Payment> searchPayments(
            @ModelAttribute PaymentFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return paymentService.searchPaged(filter, pageable);
    }
}
