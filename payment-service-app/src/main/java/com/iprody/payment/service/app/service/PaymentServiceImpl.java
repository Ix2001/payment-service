package com.iprody.payment.service.app.service;

import com.iprody.payment.service.app.mapper.PaymentMapper;
import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.models.Payment;
import com.iprody.payment.service.app.persistence.PaymentFilter;
import com.iprody.payment.service.app.persistence.PaymentFilterFactory;
import com.iprody.payment.service.app.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public PaymentDto get(UUID id) {
        Payment payment = getById(id);
        return paymentMapper.toDto(payment);
    }

    @Override
    public Page<PaymentDto> search(PaymentFilter filter, Pageable pageable) {
        Specification<Payment> spec = PaymentFilterFactory.fromFilter(filter);
        Page<Payment> page = paymentRepository.findAll(spec, pageable);
        return page.map(paymentMapper::toDto);
    }

    @Transactional
    @Override
    public PaymentDto create(PaymentDto paymentDto) {
        Payment payment = paymentMapper.toEntity(paymentDto);
        paymentRepository.save(payment);
        return paymentMapper.toDto(payment);
    }

    @Override
    @Transactional
    public PaymentDto update(UUID id, PaymentDto paymentDto) {
        Payment payment = getById(id);
        paymentMapper.updateEntity(paymentDto, payment);
        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toDto(savedPayment);
    }

    @Override
    @Transactional
    public PaymentDto updateNote(UUID id, String note) {
        Payment payment = getById(id);
        payment.setNote(note);
        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toDto(savedPayment);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Payment payment = getById(id);
        paymentRepository.delete(payment);
    }

    private Payment getById(UUID id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Платеж не найден: " + id));
    }
}
